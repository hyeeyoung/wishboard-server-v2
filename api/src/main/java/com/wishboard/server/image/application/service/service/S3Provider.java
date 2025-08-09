package com.wishboard.server.image.application.service.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.image.application.dto.request.UploadFileRequest;
import com.wishboard.server.image.client.S3FileStorageClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class S3Provider {
	private static final int MAX_FILE_SIZE = 518400; // 720 * 720

	private final S3FileStorageClient fileStorageClient;

	public String uploadFile(UploadFileRequest request, MultipartFile file) {
		request.validateAvailableContentType(file.getContentType());
		request.validateImageMaxSize(file, MAX_FILE_SIZE);
		String fileName = request.getFileNameWithBucketDirectory(file.getOriginalFilename());
		fileStorageClient.uploadFile(file, fileName);
		return fileStorageClient.getFileUrl(fileName);
	}

	public void deleteFile(String fileName) {
		if (fileName != null) {
			String[] image = fileName.split(".com/");
			fileStorageClient.deleteFile(image[1]);
		}
	}
}
