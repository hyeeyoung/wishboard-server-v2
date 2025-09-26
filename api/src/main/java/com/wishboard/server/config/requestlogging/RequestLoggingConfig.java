package com.wishboard.server.config.requestlogging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggingConfig {
	public static int MAX_PAYLOAD_LENGTH = Integer.MAX_VALUE;

	@Bean
	public RequestLoggingFilter requestLoggingFilter() {
		RequestLoggingFilter filter = new RequestLoggingFilter();
		filter.setIncludeClientInfo(true);
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(MAX_PAYLOAD_LENGTH);
		filter.setIncludeHeaders(true);
		return filter;
	}
}
