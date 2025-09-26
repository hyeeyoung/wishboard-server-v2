package com.wishboard.server.item.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetItemInfoUseCase {
	private final UserReader userReader;

	private final NotificationsRepository notificationsRepository;
	private final ItemReader itemReader;

	public ItemFolderNotificationDto execute(Long userId, Long itemId) {
		var user = userReader.findById(userId);
		var item = itemReader.findById(itemId, userId);
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item)).orElse(null);
		return ItemFolderNotificationDto.of(item, notificationsByItem);
	}
}
