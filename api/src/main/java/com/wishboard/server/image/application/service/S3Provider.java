package com.wishboard.server.image.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.image.application.dto.request.UploadFileRequest;
import com.wishboard.server.image.client.FileStorageClient;
import com.wishboard.server.image.client.S3FileStorageClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Provider {
	private static final int MAX_FILE_BYTES = 10 * 1024 * 1024; // 10MB

	private final S3FileStorageClient fileStorageClient;

	/**
	 * 기존 코드와 호환성을 위한 메서드 (영구 보관)
	 */
	public String uploadFile(UploadFileRequest request, MultipartFile file) {
		return uploadPermanentFile(request, file);
	}

	/**
	 * 영구 보관용 파일 업로드
	 */
	public String uploadPermanentFile(UploadFileRequest request, MultipartFile file) {
		return uploadFileWithDeleteTags(request, file, null); // expireDays가 null이면 영구 보관
	}

	/**
	 * 태그와 함께 파일 업로드하는 내부 메서드
	 */
	private String uploadFileWithDeleteTags(UploadFileRequest request, MultipartFile file, Integer expireDays) {
		request.validateAvailableContentType(file.getContentType());
		request.validateImageMaxSize(file, MAX_FILE_BYTES);
		String fileName = request.getFileNameWithBucketDirectory(file.getOriginalFilename());

		Map<String, String> tags = createDeleteTags(expireDays);

		// 태그가 있으면 태그와 함께 업로드, 없으면 기본 업로드
		if (tags.isEmpty()) {
			fileStorageClient.uploadFile(file, fileName);
		} else {
			fileStorageClient.uploadFile(file, fileName, tags);
		}

		return fileStorageClient.getFileUrl(fileName);
	}

	/**
	 * 기존 파일을 삭제 예정으로 마킹 (태그 변경)
	 */
	public void markFileForDeletion(String fileUrl, int expireDays) {
		if (fileUrl == null) {
			return;
		}

		try {
			String fileName = extractFileNameFromUrl(fileUrl);
			Map<String, String> deleteTags = createDeleteTags(expireDays);
			fileStorageClient.updateFileTags(fileName, deleteTags);
			log.info("File marked for deletion: {} (expires in {} days)", fileName, expireDays);
		} catch (Exception e) {
			log.warn("Failed to mark file for deletion: {}", fileUrl, e);
		}
	}


	/**
	 * 파일 즉시 삭제
	 */
	public void deleteFile(String fileName) {
		if (fileName != null) {
			String[] image = fileName.split(".com/");
			fileStorageClient.deleteFile(image[1]);
		}
	}

	/**
	 * URL에서 파일명 추출
	 */
	private String extractFileNameFromUrl(String fileUrl) {
		if (fileUrl != null && fileUrl.contains(".com/")) {
			String[] parts = fileUrl.split(".com/");
			return parts[1];
		}
		return fileUrl;
	}

	/**
	 * 삭제 마킹용 태그 생성
	 */
	private Map<String, String> createDeleteTags(int expireDays) {
		Map<String, String> tags = new HashMap<>();
		tags.put("expire-days", String.valueOf(expireDays));
		tags.put("type", "marked-for-deletion");
		tags.put("marked-date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
		return tags;
	}
}
