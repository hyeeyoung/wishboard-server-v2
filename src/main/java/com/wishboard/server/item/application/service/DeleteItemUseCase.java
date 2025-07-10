package com.wishboard.server.item.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.item.domain.repository.ItemRepository;
// import com.wishboard.server.notifications.domain.model.NotificationId; // Removed
import com.wishboard.server.notifications.domain.model.Notifications; // Added for type hint
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
// import com.wishboard.server.image.application.dto.service.S3Provider; // Remove this
import com.wishboard.server.common.application.port.out.FileStorageService; // Add this
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeleteItemUseCase {
	private final UserReader userReader;
	private final ItemReader itemReader;

	// private final S3Provider s3Provider; // Remove this
	private final FileStorageService fileStorageService; // Add this

	private final ItemRepository itemRepository;
	private final NotificationsRepository notificationsRepository;

	public void execute(Long userId, Long itemId) {
		var user = userReader.findById(userId);
		var item = itemReader.findById(itemId, user.getId());

		// 이미지 삭제
		item.getImages().forEach(image -> {
			if (StringUtils.hasText(image.getItemImageUrl())) {
				fileStorageService.deleteFile(image.getItemImageUrl()); // Changed s3Provider to fileStorageService
			}
		});
		item.getImages().clear();

		// 알림 삭제
		// var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item)); // Old way
		// notificationsByItem.ifPresent(notificationsRepository::delete); // Old way

		// New way: Find by userId and itemId, then delete.
		// Note: userId is already available. item.getUser().getId() could also be used if 'user' object isn't directly available.
		// However, the 'user' object IS available here from userReader.findById(userId).
		java.util.Optional<Notifications> notificationOptional = notificationsRepository.findByUserIdAndItemId(user.getId(), item.getId());
		notificationOptional.ifPresent(notificationsRepository::delete);
		// Alternatively, if a direct delete method like deleteByUserIdAndItemId exists, it would be more efficient.
		// For now, assuming find then delete.

		itemRepository.delete(item);
	}
}
