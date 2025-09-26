package com.wishboard.server.notifications.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.notifications.application.dto.ItemNotificationDto;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetNotificationsAllUseCase {
	private final UserReader userReader;

	private final NotificationsRepository notificationsRepository;

	public List<ItemNotificationDto> execute(Long userId) {
		var user = userReader.findById(userId);
		return notificationsRepository.findAllByUserId(user.getId());
	}

}
