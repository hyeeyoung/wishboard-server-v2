package com.wishboard.server.service.auth;

import static com.wishboard.server.common.exception.ErrorDetailCode.*;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.UnAuthorizedException;
import com.wishboard.server.common.util.JwtUtils;
import com.wishboard.server.service.auth.dto.request.TokenRequestDto;
import com.wishboard.server.service.auth.dto.response.TokenResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenService {

	private final JwtUtils jwtProvider;

	@Transactional
	public TokenResponseDto createTokenInfo(Long userId, String deviceInfo) {
		return jwtProvider.createTokenInfo(userId, deviceInfo);
	}

	@Transactional
	public TokenResponseDto getRefreshToken(TokenRequestDto request, String deviceInfo) {
		if (!jwtProvider.validateToken(request.refreshToken())) {
			throw new UnAuthorizedException(String.format("주어진 리프레시 토큰 (%s) 이 유효하지 않습니다.", request.refreshToken()), INVALID_TOKEN);
		}
		Long userId = jwtProvider.getUserIdFromJwt(request.accessToken());
		String refreshToken = jwtProvider.getRefreshToken(userId, deviceInfo);
		Boolean isLogoutDevice = jwtProvider.isLogoutDevice(userId, deviceInfo);

		if (isLogoutDevice.equals(Boolean.TRUE)) {
			throw new UnAuthorizedException(String.format("중복 로그인 기기 대수 초과(3대)로 자동으로 로그아웃 처리되었습니다. userId: %s, deviceInfo: %s", userId, deviceInfo),
				LOGOUT_BY_DEVICE_OVERFLOW);
		}

		if (Objects.isNull(refreshToken)) {
			throw new UnAuthorizedException(String.format("이미 만료된 리프레시 토큰 (%s) 입니다.", request.refreshToken()), TOKEN_EXPIRED);
		}
		if (!refreshToken.equals(request.refreshToken())) {
			throw new UnAuthorizedException(String.format("해당 리프레시 토큰의 정보 (%s) 가 일치하지 않습니다.", request.refreshToken()), INVALID_TOKEN);
		}
		return jwtProvider.createTokenInfo(userId, deviceInfo);
	}

	public void expireToken(Long userId, String deviceInfoFromHeader) {
		jwtProvider.expireRefreshToken(userId, deviceInfoFromHeader);
	}
}
