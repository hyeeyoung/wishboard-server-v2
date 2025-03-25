package com.wishboard.server.domain.notifications;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wishboard.server.domain.common.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "notifications")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notifications extends AuditingTimeEntity {

	@EmbeddedId
	private NotificationId notificationId;

	@Column(name="item_notification_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ItemNotificationType itemNotificationType;

	@Column(name="item_notification_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Seoul")
	private LocalDateTime itemNotificationDate;

	@Column(name="read_state", nullable = false)
	private Boolean readState = false;
}
