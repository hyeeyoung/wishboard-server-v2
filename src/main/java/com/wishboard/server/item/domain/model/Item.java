package com.wishboard.server.item.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.wishboard.server.common.domain.AuditingTimeEntity;
import com.wishboard.server.user.domain.model.User;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "items")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "folder_id")
	private Long folderId;

	@Column(name = "item_name", length = 512, nullable = false)
	private String itemName;

	@Column(name = "item_price", length = 255, nullable = false)
	private String itemPrice = "0";

	@Column(name = "item_url", length = 1024)
	private String itemUrl;

	@Column(name = "item_memo", columnDefinition = "TEXT")
	private String itemMemo;

	@Column(name = "add_type", length = 45)
	@Enumerated(EnumType.STRING)
	private AddType addType;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemImage> images = new ArrayList<>();

	private Item(User user, String itemName, String itemPrice, String itemUrl, String itemMemo, AddType addType) {
		this.user = user;
		this.itemName = itemName;
		this.itemPrice = itemPrice;
		this.itemUrl = itemUrl;
		this.itemMemo = itemMemo;
		this.addType = addType;
	}

	@Builder
	public Item(User user, String itemName, String itemPrice, String itemUrl, String itemMemo, AddType addType, Long folderId) {
		this.user = user;
		this.itemName = itemName;
		this.itemPrice = itemPrice;
		this.itemUrl = itemUrl;
		this.itemMemo = itemMemo;
		this.addType = addType;
		this.folderId = folderId;
	}

	public static Item newInstance(User user, String itemName, String itemPrice, String itemUrl, String itemMemo, AddType addType) {
		return new Item(user, itemName, itemPrice, itemUrl, itemMemo, addType);
	}

	public void addItemImage(List<ItemImage> images) {
		for (ItemImage image : images) {
			image.updateItem(this);
			this.images.add(image);
		}
	}

	public void updateFolderId(Long folderId) {
		// Optional: Check if the new folderId is the same as the current one.
		// if (this.folderId != null && this.folderId.equals(folderId)) {
		//     return;
		// }
		this.folderId = folderId;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void updateItemInfo(String itemName, String itemPrice, String itemUrl, String itemMemo) {
		this.itemName = itemName;
		this.itemPrice = itemPrice;
		this.itemUrl = itemUrl;
		this.itemMemo = itemMemo;
	}
}
