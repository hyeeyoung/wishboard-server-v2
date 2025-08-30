package com.wishboard.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDetailCode {

	/**
	 * 401 Unauthorized
	 */
	LOGOUT_BY_DEVICE_OVERFLOW("기기 수 초과로 인한 자동 로그아웃"),
	TOKEN_EXPIRED("만료된 토큰"),
	INVALID_TOKEN("유효하지 않은 토큰"),
	NOT_FOUND_USER("탈퇴하였거나 존재하지 않는 유저");

	private final String description;
}
