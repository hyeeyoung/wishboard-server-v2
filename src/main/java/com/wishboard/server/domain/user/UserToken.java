package com.wishboard.server.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.wishboard.server.domain.folder.Folder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user_token")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "fcm_token", length = 255)
	private String fcmToken;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public UserToken(String fcmToken, User user) {
		this.fcmToken = fcmToken;
		this.user = user;
	}
}
