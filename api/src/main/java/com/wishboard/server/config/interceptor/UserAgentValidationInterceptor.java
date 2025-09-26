package com.wishboard.server.config.interceptor;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.wishboard.server.common.exception.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserAgentValidationInterceptor implements HandlerInterceptor {
	private static final String USER_AGENT_FORMAT_REGEX = "^wishboard-(ios|aos|server)/(prod|dev|local)$";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestURI = request.getRequestURI();

		// Swagger 페이지 접근 요청은 검사하지 않음
		if (!isWishboardApi(requestURI)) {
			return true;
		}

		String userAgent = request.getHeader("User-Agent");
		if (!StringUtils.hasText(userAgent)) {
			throw new ValidationException(String.format("User-Agent does not have OS information. User-Agent=%s", userAgent),
				VALIDATION_USER_AGENT_EXCEPTION);
		}

		Pattern pattern = Pattern.compile(USER_AGENT_FORMAT_REGEX);
		if (!pattern.matcher(userAgent).matches()) {
			throw new ValidationException(String.format("Invalid User-Agent format. User-Agent=%s", userAgent), VALIDATION_USER_AGENT_EXCEPTION);
		}
		
		return true;
	}

	private boolean isWishboardApi(String uri) {
		return uri.startsWith("/v2");
	}
}
