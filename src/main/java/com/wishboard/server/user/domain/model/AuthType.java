package com.wishboard.server.user.domain.model;

import com.wishboard.server.common.model.EnumModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthType implements EnumModel {
	INTERNAL("INTERNAL"),
	SOCIAL("SOCIAL");

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
