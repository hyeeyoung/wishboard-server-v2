package com.wishboard.server.service.folder;

import static com.wishboard.server.common.exception.ErrorCode.*;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.domain.folder.Folder;
import com.wishboard.server.domain.folder.repository.FolderRepository;
import com.wishboard.server.domain.user.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FolderServiceUtils {

	public static Folder findFolderById(FolderRepository folderRepository, Long folderId) {
		return folderRepository.findFolderById(folderId)
			.orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 폴더 (%s) 입니다", folderId), NOT_FOUND_FOLDER_EXCEPTION));
	}

	public static Folder findFolderByIdAndUserId(FolderRepository folderRepository, Long folderId, User user) {
		return folderRepository.findFolderByIdAndUser(folderId, user)
			.orElseThrow(
				() -> new NotFoundException(String.format("해당 유저 (%s) 가 생성하지 않는 폴더 (%s) 입니다", user.getId(), folderId), NOT_FOUND_FOLDER_EXCEPTION));
	}
}
