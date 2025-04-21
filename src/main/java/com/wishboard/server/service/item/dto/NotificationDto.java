package com.wishboard.server.service.item.dto;

import java.time.LocalDateTime;

import com.wishboard.server.domain.notifications.ItemNotificationType;
import com.wishboard.server.domain.notifications.NotificationId;

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
