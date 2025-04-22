package com.wishboard.server.controller.notifications;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.interceptor.Auth;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.controller.notifications.response.ItemNotificationResponse;
import com.wishboard.server.service.notifications.NotificationsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationsController implements NotificationsControllerDocs {
	private final NotificationsService notificationsService;
	private final ModelMapper modelMapper;

	@Auth
	@GetMapping("/v2/noti")
	@Override
	public SuccessResponse<List<ItemNotificationResponse>> getAllNotiInfo(@UserId Long userId) {
		var itemNotificationDto = notificationsService.getNotificationsHistory(userId);
		var response = itemNotificationDto.stream()
			.map(item -> modelMapper.map(item, ItemNotificationResponse.class))
			.toList();
		return SuccessResponse.success(SuccessCode.NOTIFICATION_HISTORY_LOAD_SUCCESS, response);
	}

	@Auth
	@PutMapping("/v2/noti/{itemId}/read-state")
	@Override
	public SuccessResponse<Object> updateNotificationsState(@UserId Long userId, @PathVariable Long itemId) {
		notificationsService.updateNotificationsState(userId, itemId);
		return SuccessResponse.success(SuccessCode.NOTIFICATIONS_READ_STATE_UPDATE_SUCCESS, null);
	}

	@Auth
	@GetMapping("/v2/noti/calendar")
	@Override
	public SuccessResponse<List<ItemNotificationResponse>> getNotificationsCalendar(Long userId) {
		var itemNotificationDto = notificationsService.getAllNotifications(userId);
		var response = itemNotificationDto.stream()
			.map(item -> modelMapper.map(item, ItemNotificationResponse.class))
			.toList();
		return SuccessResponse.success(SuccessCode.NOTIFICATIONS_LIST_SUCCESS, response);
	}
}
