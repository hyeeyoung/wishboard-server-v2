package com.wishboard.server.item.domain.model;

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

@Table(name = "item_image")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage extends AuditingTimeEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "item_img", length = 522)
	private String itemImg;

	@Column(name = "item_img_url", length = 1000)
	private String itemImageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	public ItemImage(String itemImg, String itemImageUrl, Item item) {
		this.itemImg = itemImg;
		this.itemImageUrl = itemImageUrl;
		this.item = item;
	}

	public void updateItem(Item item) {
		this.item = item;
	}
}
