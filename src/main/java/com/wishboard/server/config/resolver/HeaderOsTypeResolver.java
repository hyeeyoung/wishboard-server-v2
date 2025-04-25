package com.wishboard.server.config.resolver;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.regex.Pattern;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.wishboard.server.common.exception.ForbiddenException;
import com.wishboard.server.domain.user.OsType;

import io.micrometer.common.util.StringUtils;

@Component
public class HeaderOsTypeResolver implements HandlerMethodArgumentResolver {
	private final String USER_AGENT_FORMAT_REGEX = "^wishboard-(ios|aos|server)/(prod|dev|local)$";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(HeaderOsType.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String userAgent = webRequest.getHeader("User-Agent");
		if (StringUtils.isBlank(userAgent)) {
			throw new ForbiddenException(String.format("User-Agent does not have OS information. User-Agent=%s", userAgent),
				FORBIDDEN_USER_AGENT_EXCEPTION);
		}

		Pattern pattern = Pattern.compile(USER_AGENT_FORMAT_REGEX);
		if (!pattern.matcher(userAgent).matches()) {
			throw new ForbiddenException(String.format("Invalid User-Agent format. User-Agent=%s", userAgent), FORBIDDEN_USER_AGENT_EXCEPTION);
		}
		return OsType.fromUserAgent(userAgent);
	}
}
