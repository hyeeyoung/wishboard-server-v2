package com.wishboard.server.domain.user;

import static com.wishboard.server.common.exception.ErrorCode.*;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.model.EnumModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OsType implements EnumModel {
	IOS("IOS"),
	AOS("AOS"),
	SERVER("SERVER");

	private final String value;

	public static OsType fromUserAgent(String userAgent) {
		String osSegment = userAgent.split("/")[0].split("-")[1].toUpperCase();
		try {
			return OsType.valueOf(osSegment);
		} catch (IllegalArgumentException e) {
			throw new ValidationException(String.format("Invalid OS Type: %s", userAgent), VALIDATION_HEADER_USER_AGENT_EXCEPTION);
		}
	}

	@Override
	public String getKey() {
		return name();
	}

	@Override
	public String getValue() {
		return value;
	}
}
