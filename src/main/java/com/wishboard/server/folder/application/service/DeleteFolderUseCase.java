package com.wishboard.server.folder.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.folder.domain.repository.FolderRepository;
import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeleteFolderUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;

	private final FolderRepository folderRepository;
	private final ItemRepository itemRepository;

	public void execute(Long userId, Long folderId) {
		var user = userReader.findById(userId);
		var folder = folderReader.findByIdAndUserId(folderId, user.getId()); // Changed to use userId
		var items = itemRepository.findAllByFolderId(folder.getId()); // Changed to use folderId
		if (!items.isEmpty()) {
			items.forEach(item -> item.updateFolderId(null)); // Changed to updateFolderId
		}
		folderRepository.delete(folder);
	}
}
