package com.wishboard.server.user.domain.model;

import com.wishboard.server.common.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user_token")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken extends AuditingTimeEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fcm_token", length = 255)
	private String fcmToken;

	@Column(name = "device", length = 255)
	private String device;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public UserToken(String fcmToken, String deviceInfo, User user) {
		this.fcmToken = fcmToken;
		this.device = deviceInfo;
		this.user = user;
	}
}
