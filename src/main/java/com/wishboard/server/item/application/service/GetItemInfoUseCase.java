package com.wishboard.server.item.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.application.service.support.ItemReader;
// import com.wishboard.server.notifications.domain.model.NotificationId; // Removed
import com.wishboard.server.notifications.domain.model.Notifications; // Added for type hint
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetItemInfoUseCase {
	// private final UserReader userReader; // User object not strictly needed for this specific logic if userId is passed

	private final NotificationsRepository notificationsRepository;
	private final ItemReader itemReader;

	public ItemFolderNotificationDto execute(Long userId, Long itemId) {
		// var user = userReader.findById(userId); // Not strictly needed if itemReader and notificationsRepository use userId
		var item = itemReader.findById(itemId, userId); // Assuming itemReader takes userId for validation
		
		Notifications notifications = notificationsRepository.findByUserIdAndItemId(userId, item.getId()).orElse(null);
		
		if (notifications != null) {
			return ItemFolderNotificationDto.of(item, notifications.getItemNotificationType(), notifications.getItemNotificationDate());
		} else {
			return ItemFolderNotificationDto.of(item, null, null);
		}
	}
}
