package com.wishboard.server.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.wishboard.server.domain.cart.Cart;
import com.wishboard.server.domain.common.AuditingTimeEntity;
import com.wishboard.server.domain.folder.Folder;
import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.notifications.Notifications;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(length = 512)
	private String nickname;

	@Column(name = "fcm_token", length = 255, unique = true)
	private String fcmToken;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "push_state", nullable = false)
	private Boolean pushState = false;

	@Column(name = "auth_type", nullable = false, length = 45, columnDefinition = "default 'internal'")
	@Enumerated(EnumType.STRING)
	private AuthType authType;

	@Column(name = "os_type", nullable = false, length = 45)
	@Enumerated(EnumType.STRING)
	private OsType osType;

	@Embedded
	private SocialInfo socialInfo;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Folder> folders = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> items = new ArrayList<>();

	@OneToMany(mappedBy = "cartId.user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Cart> carts = new ArrayList<>();

	@OneToMany(mappedBy = "notificationId.user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notifications> notifications = new ArrayList<>();

	private User(String socialId, UserProviderType socialType) {
		this.socialInfo = SocialInfo.of(socialId, socialType);
	}

	public static User newInstance(String socialId, UserProviderType socialType) {
		return new User(socialId, socialType);
	}
}
