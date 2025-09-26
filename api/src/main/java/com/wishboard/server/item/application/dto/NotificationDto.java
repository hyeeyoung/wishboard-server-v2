package com.wishboard.server.item.application.dto;

import java.time.LocalDateTime;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.notifications.domain.model.NotificationId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
	private NotificationId notificationId;
	private ItemNotificationType itemNotificationType;
	private LocalDateTime itemNotificationDate;
	private Boolean readState;
}
