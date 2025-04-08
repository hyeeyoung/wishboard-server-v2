package com.wishboard.server.domain.notifications;

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
}
