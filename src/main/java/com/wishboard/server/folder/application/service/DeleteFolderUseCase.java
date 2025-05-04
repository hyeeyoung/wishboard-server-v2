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
		var folder = folderReader.findByIdAndUser(folderId, user);
		var items = itemRepository.findAllByFolder(folder);
		if (!items.isEmpty()) {
			items.forEach(item -> item.updateFolder(null));
		}
		folderRepository.delete(folder);
	}
}
