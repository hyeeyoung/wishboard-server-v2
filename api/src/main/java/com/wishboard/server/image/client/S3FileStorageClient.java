package com.wishboard.server.image.client;

import com.wishboard.server.common.exception.InternalServerException;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3FileStorageClient implements FileStorageClient {

	private final S3Template s3Template; // 업로드/삭제는 그대로 사용
	private final S3Client s3Client;     // URL 생성용 (만료 없음)

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Override
	public void uploadFile(MultipartFile file, String fileName) {
		try (InputStream in = file.getInputStream()) {
			ObjectMetadata meta = ObjectMetadata.builder()
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.build();

			// 퍼블릭 공개는 "버킷 정책"으로 처리. 여기선 ACL 건드리지 않음.
			s3Template.upload(bucket, fileName, in, meta);
		} catch (IOException e) {
			log.error("file stream read failed. fileName: {}", file.getOriginalFilename(), e);
			throw new InternalServerException(
				String.format("파일 (%s) 입력 스트림을 가져오는 중 에러가 발생했습니다", file.getOriginalFilename())
			);
		} catch (S3Exception e) {
			log.error("S3 file upload failed for file: {}", file.getOriginalFilename(), e);
			throw new InternalServerException(
				String.format("파일 (%s) 업로드 중 내부 에러가 발생했습니다", file.getOriginalFilename())
			);
		}
	}

	public void deleteFile(String fileName) {
		try {
			s3Template.deleteObject(bucket, fileName);
		} catch (S3Exception e) {
			log.error("S3 file delete failed for file: {}", fileName, e);
			throw new InternalServerException(
				String.format("파일 (%s) 삭제 중 내부 에러가 발생했습니다", fileName)
			);
		}
	}

	@Override
	public String getFileUrl(String fileName) {

		URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.build());
		return url.toString();
	}
}
