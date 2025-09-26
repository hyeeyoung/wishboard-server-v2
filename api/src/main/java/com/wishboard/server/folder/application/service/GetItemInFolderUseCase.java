package com.wishboard.server.folder.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GetItemInFolderUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;

	private final FolderRepository folderRepository;

	public Page<ItemFolderNotificationDto> execute(Long userId, Long folderId, Pageable pageable) {
		var user = userReader.findById(userId);
		var folder = folderReader.findByIdAndUser(folderId, user);
		return folderRepository.findItemListByUserIdAndFolderId(user.getId(), folder.getId(), pageable);
	}

}
