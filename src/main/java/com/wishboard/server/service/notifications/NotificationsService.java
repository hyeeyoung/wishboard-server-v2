package com.wishboard.server.service.notifications;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.domain.item.repository.ItemRepository;
import com.wishboard.server.domain.notifications.NotificationId;
import com.wishboard.server.domain.notifications.repository.NotificationsRepository;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.item.ItemServiceUtils;
import com.wishboard.server.service.notifications.dto.ItemNotificationDto;
import com.wishboard.server.service.user.UserServiceUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class NotificationsService {
	private final UserRepository userRepository;
	private final NotificationsRepository notificationsRepository;
	private final ItemRepository itemRepository;

	public List<ItemNotificationDto> getNotificationsHistory(Long userId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		return notificationsRepository.findUpcomingNotificationsByUserId(user.getId());
	}

	public List<ItemNotificationDto> getAllNotifications(Long userId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		return notificationsRepository.findAllByUserId(user.getId());
	}

	public void updateNotificationsState(Long userId, Long itemId) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var item = ItemServiceUtils.findItemById(itemRepository, itemId, user.getId());
		var notifications = notificationsRepository.findByNotificationId(new NotificationId(user, item))
			.orElseThrow(() -> new NotFoundException(String.format("존재하지 않는 알림 (userId: %s, itemId: %s) 입니다.", user.getId(), item.getId()),
				NOT_FOUND_NOTIFICATION_EXCEPTION));
		notifications.confirmRead();
	}
}
