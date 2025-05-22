package com.wishboard.server.folder.application.service.support;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FolderReader {
	private final FolderRepository folderRepository;

	public Folder findByIdAndUserId(Long folderId, Long userId) {
		return folderRepository.findFolderByIdAndUserId(folderId, userId)
			.orElseThrow(
				() -> new NotFoundException(String.format("해당 유저 (%s) 가 생성하지 않는 폴더 (%s) 입니다", userId, folderId), NOT_FOUND_FOLDER_EXCEPTION));
	}
}
