package com.wishboard.server.config.requestlogging;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		log.debug(message);
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		// nothing
	}
}
