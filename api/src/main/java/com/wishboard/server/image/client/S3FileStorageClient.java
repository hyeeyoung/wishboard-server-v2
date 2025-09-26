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
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class S3FileStorageClient implements FileStorageClient {

	private final S3Template s3Template; // 기본 업로드/삭제용
	private final S3Client s3Client;     // 태그 관련 작업용

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Override
	public void uploadFile(MultipartFile file, String fileName) {
		try (InputStream in = file.getInputStream()) {
			ObjectMetadata meta = ObjectMetadata.builder()
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.build();

			s3Template.upload(bucket, fileName, in, meta);
			log.debug("File uploaded successfully without tags: {}", fileName);
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

	@Override
	public void uploadFile(MultipartFile file, String fileName, Map<String, String> tags) {
		try (InputStream inputStream = file.getInputStream()) {

			// 태그 생성
			List<Tag> s3Tags = tags.entrySet().stream()
				.map(entry -> Tag.builder()
					.key(entry.getKey())
					.value(entry.getValue())
					.build())
				.collect(Collectors.toList());

			Tagging tagging = Tagging.builder()
				.tagSet(s3Tags)
				.build();

			// S3Client를 사용하여 태그와 함께 업로드
			PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.tagging(tagging)
				.build();

			s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

			log.debug("@@ File uploaded successfully with tags: {}, tags: {}", fileName, tags);

		} catch (IOException e) {
			log.error("@@ file stream read failed. fileName: {}", file.getOriginalFilename(), e);
			throw new InternalServerException(
				String.format("파일 (%s) 입력 스트림을 가져오는 중 에러가 발생했습니다", file.getOriginalFilename())
			);
		} catch (Exception e) {
			log.error("@@ S3 file upload with tags failed for file: {}", file.getOriginalFilename(), e);
			throw new InternalServerException(
				String.format("파일 (%s) 태그와 함께 업로드 중 내부 에러가 발생했습니다", file.getOriginalFilename())
			);
		}
	}

	@Override
	public void deleteFile(String fileName) {
		try {
			s3Template.deleteObject(bucket, fileName);
			log.debug("@@ File deleted successfully: {}", fileName);
		} catch (S3Exception e) {
			log.error("@@ S3 file delete failed for file: {}", fileName, e);
			throw new InternalServerException(
				String.format("파일 (%s) 삭제 중 내부 에러가 발생했습니다", fileName)
			);
		}
	}

	@Override
	public String getFileUrl(String fileName) {
		try {
			URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.build());
			return url.toString();
		} catch (Exception e) {
			log.error("@@ Failed to generate URL for file: {}", fileName, e);
			throw new InternalServerException(
				String.format("파일 (%s) URL 생성 중 내부 에러가 발생했습니다", fileName)
			);
		}
	}

	@Override
	public void updateFileTags(String fileName, Map<String, String> tags) {
		try {
			// 기존 태그 조회
			GetObjectTaggingRequest getTagRequest = GetObjectTaggingRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.build();

			GetObjectTaggingResponse getTagResponse;
			Map<String, String> existingTags;

			try {
				getTagResponse = s3Client.getObjectTagging(getTagRequest);
				existingTags = getTagResponse.tagSet().stream()
					.collect(Collectors.toMap(Tag::key, Tag::value));
			} catch (Exception e) {
				// 태그가 없거나 파일이 없는 경우 빈 맵으로 시작
				log.warn("@@ Could not get existing tags for file: {}, proceeding with new tags only", fileName);
				existingTags = Map.of();
			}

			// 기존 태그와 새 태그 병합 (새 태그가 우선)
			Map<String, String> mergedTags = existingTags.entrySet().stream()
				.collect(Collectors.toMap(
					Map.Entry::getKey,
					Map.Entry::getValue
				));
			mergedTags.putAll(tags);

			// 새로운 태그 설정
			List<Tag> newTags = mergedTags.entrySet().stream()
				.map(entry -> Tag.builder()
					.key(entry.getKey())
					.value(entry.getValue())
					.build())
				.collect(Collectors.toList());

			PutObjectTaggingRequest putTagRequest = PutObjectTaggingRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.tagging(Tagging.builder().tagSet(newTags).build())
				.build();

			s3Client.putObjectTagging(putTagRequest);

			log.info("File tags updated successfully: {}, new tags: {}", fileName, tags);

		} catch (Exception e) {
			log.error("Failed to update tags for file: {}", fileName, e);
			throw new InternalServerException(
				String.format("파일 (%s) 태그 업데이트 중 내부 에러가 발생했습니다", fileName)
			);
		}
	}
}
