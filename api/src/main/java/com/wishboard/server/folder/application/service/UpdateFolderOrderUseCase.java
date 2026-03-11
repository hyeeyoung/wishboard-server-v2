package com.wishboard.server.folder.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateFolderOrderUseCase {
	private final UserReader userReader;
	private final FolderRepository folderRepository;

	public void execute(Long userId, List<Long> orderedFolderIds) {
		var user = userReader.findById(userId);
		var folders = folderRepository.findAllByUser(user);

		validateFolderOrderRequest(folders.stream().map(Folder::getId).toList(), orderedFolderIds);

		Map<Long, Long> orderByFolderId = IntStream.range(0, orderedFolderIds.size())
			.boxed()
			.collect(Collectors.toMap(
				orderedFolderIds::get,
				index -> (long) index + 1
			));

		folders.forEach(folder -> folder.updateFolderOrder(orderByFolderId.get(folder.getId())));
	}

	private void validateFolderOrderRequest(List<Long> userFolderIds, List<Long> orderedFolderIds) {
		if (orderedFolderIds.size() != new HashSet<>(orderedFolderIds).size()) {
			throw new ValidationException("요청된 폴더 ID 목록에 중복이 있습니다.", VALIDATION_FOLDER_ID_DUPLICATE_EXCEPTION);
		}

		var userFolderIdSet = new HashSet<>(userFolderIds);
		var orderedFolderIdSet = new HashSet<>(orderedFolderIds);

		if (userFolderIdSet.size() != orderedFolderIdSet.size()) {
			throw new ValidationException("사용자의 전체 폴더 ID를 모두 전달해야 합니다.", VALIDATION_FOLDER_ID_REQUIRED_ALL_EXCEPTION);
		}

		if (!orderedFolderIdSet.containsAll(userFolderIdSet)) {
			throw new ValidationException("요청된 폴더 ID 목록에 유효하지 않은 값이 있습니다.", VALIDATION_FOLDER_ID_INVALID_EXCEPTION);
		}
	}
}
