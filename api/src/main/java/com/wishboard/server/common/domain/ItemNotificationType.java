package com.wishboard.server.common.domain;

import com.wishboard.server.common.model.EnumModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ItemNotificationType implements EnumModel {
	SALE_START("SALE_START"),
	SALE_END("SALE_END"),
	REMINDER("REMINDER"),
	OPEN("OPEN"),
	RESTOCK("RESTOCK"),
	PREORDER("PREORDER");

	private final String value;

	@Override
	public String getKey() {
		return name();
	}

	@Override
	public String getValue() {
		return value;
	}

	public ItemNotificationType valueOfKey(String key) {
		for (ItemNotificationType type : ItemNotificationType.values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid key: " + key);
	}
}
