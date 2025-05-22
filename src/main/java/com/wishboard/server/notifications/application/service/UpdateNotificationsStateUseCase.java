package com.wishboard.server.notifications.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.item.application.service.support.ItemReader;
import com.wishboard.server.notifications.application.dto.ItemNotificationDto;
// import com.wishboard.server.notifications.domain.model.NotificationId; // Removed
import com.wishboard.server.notifications.domain.model.Notifications; // Added for type hint
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdateNotificationsStateUseCase {
	private final UserReader userReader;
	private final ItemReader itemReader;

	private final NotificationsRepository notificationsRepository;

	public List<ItemNotificationDto> execute(Long userId, Long itemId) {
		var user = userReader.findById(userId); // user object is available
		// var item = itemReader.findById(itemId, userId); // item object is not strictly needed for the notification fetch if we use itemId directly

		// Fetch the notification using userId and itemId
		Notifications notification = notificationsRepository.findByUserIdAndItemId(userId, itemId)
			.orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 알림 (userId: %s, itemId: %s) 입니다.", userId, itemId),
				NOT_FOUND_NOTIFICATION_EXCEPTION));
		
		notification.confirmRead();
		// notificationsRepository.save(notification); // Not strictly necessary if @Transactional manages the update
		
		return notificationsRepository.findUpcomingNotificationsByUserId(userId); // Use userId directly
	}

}
