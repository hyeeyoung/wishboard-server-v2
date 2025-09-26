package com.wishboard.server.image.application.dto.request;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.type.FileType;

public interface UploadFileRequest {

	FileType getType();

	default void validateAvailableContentType(String contentType) {
		getType().validateAvailableContentType(contentType);
	}

	default String getFileNameWithBucketDirectory(String originalFileName) {
		return getType().createUniqueFileNameWithExtension(originalFileName);
	}

	default void validateImageMaxSize(final MultipartFile file, final int maxFileSize) {
		if (file.getSize() > maxFileSize) {
			throw new ValidationException(
				String.format("이미지 크기 (%s) 가 최대 사이즈 (%s) 보다 큽니다.", file.getSize(), maxFileSize),
				VALIDATION_IMAGE_SIZE_EXCEPTION);
		}
	}
}
