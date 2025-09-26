package com.wishboard.server.user.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.wishboard.server.common.domain.AuditingTimeEntity;
import com.wishboard.server.common.domain.OsType;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.notifications.domain.model.Notifications;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 256, unique = true)
	private String email;

	@Column(nullable = false, length = 256)
	private String password;

	@Column(name = "profile_img", length = 512)
	private String profileImg;

	@Column(name = "profile_img_url", length = 1000)
	private String profileImgUrl;

	@Column(length = 512)
	private String nickname;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "push_state", nullable = false)
	private Boolean pushState = false;

	@Column(name = "auth_type", nullable = false, length = 45)
	@Enumerated(EnumType.STRING)
	private AuthType authType;

	@Column(name = "os_type", nullable = false, length = 45)
	@Enumerated(EnumType.STRING)
	private OsType osType;

	@Embedded
	private SocialInfo socialInfo;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserToken> fcmTokens = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Folder> folders = new ArrayList<>();

	@OneToMany(mappedBy = "notificationId.user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notifications> notifications = new ArrayList<>();

	private User(String socialId, AuthType authType, UserProviderType socialType) {
		this.authType = authType;
		this.socialInfo = SocialInfo.of(socialId, socialType);
	}

	private User(String email, String password, AuthType authType, OsType osType) {
		this.email = email;
		this.password = password;
		this.authType = authType;
		this.osType = osType;
	}

	// TODO 소셜 로그인 시만 사용 우선
	public static User newInstance(String socialId, AuthType authType, UserProviderType socialType) {
		return new User(socialId, authType, socialType);
	}

	public static void addFcmToken(String fcmToken, String deviceInfo, User user) {
		UserToken userToken = new UserToken(fcmToken, deviceInfo, user);
		user.getFcmTokens().add(userToken);
	}

	public static User newInstance(String email, String password, String fcmToken, String deviceInfo, AuthType authType, OsType osType) {
		User user = new User(email, password, authType, osType);
		addFcmToken(fcmToken, deviceInfo, user);
		return user;
	}

	public void updateDeviceInformation(String fcmToken, OsType osType, String deviceInfo) {
		// 디바이스/토큰 업데이트 정책: 디바이스 당 1개 토큰 유지
		if (StringUtils.hasText(fcmToken)) {
			// 1) 같은 device에 대한 기존 엔트리 제거
			this.fcmTokens.removeIf(t -> Objects.equals(t.getDevice(), deviceInfo));
			// 2) 새 토큰 삽입
			addFcmToken(fcmToken, deviceInfo, this);
		}
		if (osType != null && !StringUtils.hasText(osType.getValue())) {
			this.osType = osType;
		}
	}

	public void updatePushState(boolean pushState) {
		if (pushState) {
			this.pushState = Boolean.TRUE;
		} else {
			this.pushState = Boolean.FALSE;
		}
	}

	public void updateUserNickname(String nickname) {
		if (this.nickname != null && this.nickname.equals(nickname)) {
			return;
		}
		this.nickname = nickname;
	}

	public void updateProfileImage(String originalFilename, String profileImageUrl) {
		if (this.profileImgUrl != null && this.profileImgUrl.equals(profileImageUrl)) {
			return;
		}
		this.profileImg = originalFilename;
		this.profileImgUrl = profileImageUrl;
	}

	public void updatePassword(String hashedPassword) {
		if (this.password != null && this.password.equals(hashedPassword)) {
			return;
		}
		this.password = hashedPassword;
	}
}
