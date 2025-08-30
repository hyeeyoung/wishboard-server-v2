package com.wishboard.server.config.interceptor;

import static com.wishboard.server.common.exception.ErrorCode.*;
import static com.wishboard.server.common.exception.ErrorDetailCode.*;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wishboard.server.auth.infrastructure.jwt.JwtClient;
import com.wishboard.server.common.exception.UnAuthorizedException;
import com.wishboard.server.user.application.service.support.UserReader;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginCheckHandler {

	private final UserReader userReader;
	private final JwtClient jwtProvider;

	public Long getUserId(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			String accessToken = bearerToken.substring("Bearer ".length());
			if (jwtProvider.validateToken(accessToken)) {
				Long userId = jwtProvider.getUserIdFromJwt(accessToken);
				if (userId == null || !userReader.existsById(userId)) {
					throw new UnAuthorizedException(String.format("존재하지 않는 유저 (%s) 입니다.", userId), NOT_FOUND_USER_EXCEPTION, NOT_FOUND_USER);
				}
				return userId;
			} else {
				throw new UnAuthorizedException(String.format("만료된 JWT (%s) 입니다.", bearerToken), UNAUTHORIZED_EXCEPTION, TOKEN_EXPIRED);
			}
		}
		throw new UnAuthorizedException(String.format("잘못된 JWT (%s) 입니다.", bearerToken), UNAUTHORIZED_INVALID_TOKEN_EXCEPTION, INVALID_TOKEN);
	}
}
