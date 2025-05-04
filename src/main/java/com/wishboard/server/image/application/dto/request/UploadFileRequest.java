package com.wishboard.server.image.application.dto.request;

import com.wishboard.server.common.type.FileType;

public interface UploadFileRequest {

	FileType getType();

	default void validateAvailableContentType(String contentType) {
		getType().validateAvailableContentType(contentType);
	}

	default String getFileNameWithBucketDirectory(String originalFileName) {
		return getType().createUniqueFileNameWithExtension(originalFileName);
	}
}
