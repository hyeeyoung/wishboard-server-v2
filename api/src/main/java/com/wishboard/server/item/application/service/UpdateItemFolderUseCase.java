package com.wishboard.server.item.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.folder.application.service.support.FolderReader;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateItemFolderUseCase {
	private final UserReader userReader;
	private final FolderReader folderReader;
	private final ItemReader itemReader;

	private final NotificationsRepository notificationsRepository;

	public ItemFolderNotificationDto execute(Long userId, Long itemId, Long folderId) {
		var user = userReader.findById(userId);
		var item = itemReader.findById(itemId, userId);
		var folder = folderReader.findByIdAndUser(folderId, user);
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item)).orElse(null);
		item.updateFolder(folder);
		return ItemFolderNotificationDto.of(item, notificationsByItem);
	}
}
