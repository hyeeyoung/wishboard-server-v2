package com.wishboard.server.item.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.image.application.dto.service.S3Provider;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeleteItemUseCase {
	private final UserReader userReader;
	private final ItemReader itemReader;

	private final S3Provider s3Provider;

	private final ItemRepository itemRepository;
	private final NotificationsRepository notificationsRepository;

	public void execute(Long userId, Long itemId) {
		var user = userReader.findById(userId);
		var item = itemReader.findById(itemId, user.getId());

		// 이미지 삭제
		item.getImages().forEach(image -> {
			if (StringUtils.hasText(image.getItemImageUrl())) {
				s3Provider.deleteFile(image.getItemImageUrl());
			}
		});
		item.getImages().clear();

		// 알림 삭제
		var notificationsByItem = notificationsRepository.findByNotificationId(new NotificationId(user, item));
		notificationsByItem.ifPresent(notificationsRepository::delete);

		itemRepository.delete(item);
	}
}
