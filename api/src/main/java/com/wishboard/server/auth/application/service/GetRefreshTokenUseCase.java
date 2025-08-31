package com.wishboard.server.auth.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;
import static com.wishboard.server.common.exception.ErrorDetailCode.*;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.application.dto.command.TokenCommand;
import com.wishboard.server.auth.infrastructure.jwt.JwtClient;
import com.wishboard.server.auth.presentation.dto.response.TokenResponseDto;
import com.wishboard.server.common.exception.UnAuthorizedException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GetRefreshTokenUseCase {

	private final JwtClient jwtClient;

	public TokenResponseDto execute(TokenCommand tokenCommand, String deviceInfo) {
		if (!jwtClient.validateToken(tokenCommand.getRefreshToken())) {
			throw new UnAuthorizedException(String.format("주어진 리프레시 토큰 (%s) 이 유효하지 않습니다.", tokenCommand.getRefreshToken()),
				UNAUTHORIZED_INVALID_TOKEN_EXCEPTION, INVALID_TOKEN);
		}
		Long userId = jwtClient.getUserIdFromJwt(tokenCommand.getAccessToken());
		String refreshToken = jwtClient.getRefreshToken(userId, deviceInfo);
		Boolean isLogoutDevice = jwtClient.isLogoutDevice(userId, deviceInfo);

		if (isLogoutDevice) {
			throw new UnAuthorizedException(String.format("중복 로그인 기기 대수 초과(3대)로 자동으로 로그아웃 처리되었습니다. userId: %s, deviceInfo: %s", userId, deviceInfo),
				UNAUTHORIZED_EXCEPTION, LOGOUT_BY_DEVICE_OVERFLOW);
		}

		if (Objects.isNull(refreshToken)) {
			throw new UnAuthorizedException(String.format("이미 만료된 리프레시 토큰 (%s) 입니다.", tokenCommand.getRefreshToken()),
				UNAUTHORIZED_EXCEPTION, TOKEN_EXPIRED);
		}
		if (!refreshToken.equals(tokenCommand.getRefreshToken())) {
			throw new UnAuthorizedException(String.format("해당 리프레시 토큰의 정보 (%s) 가 일치하지 않습니다.", tokenCommand.getRefreshToken()),
				UNAUTHORIZED_EXCEPTION, INVALID_TOKEN);
		}
		return jwtClient.createTokenInfo(userId, deviceInfo);
	}
}
