package com.wishboard.server.image.application.dto.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.image.application.dto.request.UploadFileRequest;
import com.wishboard.server.image.infrastructure.S3FileStorageClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class S3Provider {

	private final S3FileStorageClient fileStorageClient;

	public String uploadFile(UploadFileRequest request, MultipartFile file) {
		request.validateAvailableContentType(file.getContentType());
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
