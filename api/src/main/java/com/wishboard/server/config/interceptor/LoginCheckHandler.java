package com.wishboard.server.config.interceptor;

import static com.wishboard.server.common.exception.ErrorDetailCode.*;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.wishboard.server.auth.infrastructure.jwt.JwtClient;
import com.wishboard.server.common.exception.UnAuthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LoginCheckHandler {

	private final JwtClient jwtProvider;

	public Long getUserId(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			String accessToken = bearerToken.substring("Bearer ".length());
			if (jwtProvider.validateToken(accessToken)) {
				Long userId = jwtProvider.getUserIdFromJwt(accessToken);
				if (userId != null) {
					return userId;
				}
			} else {
				throw new UnAuthorizedException(String.format("만료된 JWT (%s) 입니다.", bearerToken), TOKEN_EXPIRED);
			}
		}
		throw new UnAuthorizedException(String.format("잘못된 JWT (%s) 입니다.", bearerToken), INVALID_TOKEN);
	}
}
