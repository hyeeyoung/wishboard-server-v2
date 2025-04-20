package com.wishboard.server.domain.item;

import java.util.ArrayList;
import java.util.List;

import com.wishboard.server.domain.cart.Cart;
import com.wishboard.server.domain.common.AuditingTimeEntity;
import com.wishboard.server.domain.folder.Folder;
import com.wishboard.server.domain.notifications.Notifications;
import com.wishboard.server.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Table(name = "items")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long itemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "folder_id")
	private Folder folder;

	@Column(name = "item_img", length = 512)
	private String itemImg;

	@Column(name = "item_name", length = 512, nullable = false)
	private String itemName;

	@Column(name = "item_price", length = 255, nullable = false)
	private String itemPrice = "0";

	@Column(name = "item_img_url", length = 1000)
	private String itemImageUrl;

	@Column(name = "item_url", length = 1024)
	private String itemUrl;

	@Column(name = "item_memo", columnDefinition = "TEXT")
	private String itemMemo;

	@Column(name = "add_type", length = 45)
	@Enumerated(EnumType.STRING)
	private AddType addType;

	@OneToMany(mappedBy = "cartId.item", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Cart> carts = new ArrayList<>();

	@OneToMany(mappedBy = "notificationId.item", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notifications> notifications = new ArrayList<>();
}
