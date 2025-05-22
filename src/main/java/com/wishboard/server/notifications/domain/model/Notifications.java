package com.wishboard.server.notifications.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wishboard.server.common.domain.AuditingTimeEntity;
import com.wishboard.server.common.domain.ItemNotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "notifications")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notifications extends AuditingTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "item_notification_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ItemNotificationType itemNotificationType;

	@Column(name = "item_notification_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Seoul")
	private LocalDateTime itemNotificationDate;

	@Column(name = "read_state", nullable = false)
	private Boolean readState = false;

	private Notifications(Long userId, Long itemId, ItemNotificationType itemNotificationType, LocalDateTime itemNotificationDate) {
		this.userId = userId;
		this.itemId = itemId;
		this.itemNotificationType = itemNotificationType;
		this.itemNotificationDate = itemNotificationDate;
		this.readState = Boolean.FALSE;
	}

	public static Notifications newInstance(Long userId, Long itemId, ItemNotificationType itemNotificationType,
		LocalDateTime itemNotificationDate) {
		return new Notifications(userId, itemId, itemNotificationType, itemNotificationDate);
	}

	public void updateState(ItemNotificationType itemNotificationType, LocalDateTime itemNotificationDate) {
		if (this.itemNotificationType != itemNotificationType) {
			this.itemNotificationType = itemNotificationType;
		}
		if (this.itemNotificationDate != itemNotificationDate) {
			this.itemNotificationDate = itemNotificationDate;
		}
	}

	public void confirmRead() {
		this.readState = true;
	}
}
