package com.wishboard.server.common.type;

import static com.wishboard.server.common.exception.ErrorCode.*;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.util.FileUtils;
import com.wishboard.server.common.util.UuidUtils;

import lombok.Getter;

@Getter
public enum FileType {

	ITEM_IMAGE("아이템 이미지", "", FileContentType.IMAGE),
	PROFILE_IMAGE("프로필 이미지", "", FileContentType.IMAGE);

	private final String description;
	private final String directory;
	private final FileContentType contentType;

	private FileType(String description, String directory, FileContentType contentType) {
		this.description = description;
		this.directory = directory;
		this.contentType = contentType;
	}

	public void validateAvailableContentType(String contentType) {
		this.contentType.validateAvailableContentType(contentType);
	}

	/**
	 * 파일의 기존의 확장자를 유지한 채, 유니크한 파일의 이름을 반환합니다.
	 */
	public String createUniqueFileNameWithExtension(String originalFileName) {
		if (originalFileName == null) {
			throw new ValidationException("잘못된 파일의 originFilename 입니다", FORBIDDEN_FILE_NAME_EXCEPTION);
		}
		String extension = FileUtils.getFileExtension(originalFileName);
		return getFileNameWithDirectory(UuidUtils.generate().concat(extension));
	}

	private String getFileNameWithDirectory(String fileName) {
		return this.directory.concat(fileName);
	}
}
