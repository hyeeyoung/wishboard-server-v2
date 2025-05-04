package com.wishboard.server.cart.domain.model;

import com.wishboard.server.common.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "cart")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends AuditingTimeEntity {

	@EmbeddedId
	private CartId cartId;

	@Column(name = "item_count", length = 512)
	private Integer itemCount;
}
