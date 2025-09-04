package com.wishboard.server.image.client;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {

	/**
	 * 기본 파일 업로드 (태그 없이)
	 */
	void uploadFile(MultipartFile file, String fileName);

	/**
	 * 태그와 함께 파일 업로드
	 */
	void uploadFile(MultipartFile file, String fileName, Map<String, String> tags);

	/**
	 * 파일 삭제
	 */
	void deleteFile(String fileName);

	/**
	 * 파일 URL 조회
	 */
	String getFileUrl(String fileName);

	/**
	 * 파일 태그 업데이트
	 */
	void updateFileTags(String fileName, Map<String, String> tags);
}
