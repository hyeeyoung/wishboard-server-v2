package com.wishboard.server.domain.notifications.repository;

import java.util.List;

import com.wishboard.server.service.notifications.dto.ItemNotificationDto;

public interface NotificationsRepositoryCustom {
	List<ItemNotificationDto> findAllByUserId(Long userId);

	List<ItemNotificationDto> findUpcomingNotificationsByUserId(Long userId);
}
