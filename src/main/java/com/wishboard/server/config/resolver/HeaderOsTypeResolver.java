package com.wishboard.server.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.domain.user.OsType;

import io.micrometer.common.util.StringUtils;

@Component
public class HeaderOsTypeResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(HeaderOsType.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String userAgent = webRequest.getHeader("User-Agent");
		if (StringUtils.isBlank(userAgent)) {
			throw new ValidationException(String.format("User-Agent does not have OS information. User-Agent={}", userAgent));
		}
		return OsType.fromUserAgent(userAgent);
	}
}
