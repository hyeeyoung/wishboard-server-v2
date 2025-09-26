package com.wishboard.server.notifications.domain.repository;

import java.util.List;

import com.wishboard.server.notifications.application.dto.ItemNotificationDto;

public interface NotificationsRepositoryCustom {
	List<ItemNotificationDto> findAllByUserId(Long userId);

	List<ItemNotificationDto> findUpcomingNotificationsByUserId(Long userId);

	void deleteAllByUserId(Long userId);
}
