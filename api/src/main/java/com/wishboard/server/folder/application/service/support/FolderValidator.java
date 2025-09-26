package com.wishboard.server.folder.application.service.support;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.wishboard.server.common.exception.ConflictException;
import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.user.domain.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FolderValidator {
	private final FolderRepository folderRepository;

	public void checkDuplicateFolderName(@NotNull User user, @NotBlank String folderName) {
		var folder = folderRepository.findByUserAndFolderName(user, folderName);
		if (folder.isPresent()) {
			throw new ConflictException("이미 존재하는 폴더명입니다.", CONFLICT_FOLDER_NAME_DUPLICATE_EXCEPTION);
		}
	}
}
