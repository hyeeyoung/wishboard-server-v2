package com.wishboard.server.auth.application.service.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.wishboard.server.user.domain.model.UserProviderType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SocialServiceProvider {
	private final Map<UserProviderType, ExternalAuthService> authServiceMap = new HashMap<>();

	public SocialServiceProvider(List<ExternalAuthService> authServices) {
		for (ExternalAuthService service : authServices) {
			authServiceMap.put(service.providerType(), service);
		}
	}

	public ExternalAuthService get(UserProviderType providerType) {
		if (!authServiceMap.containsKey(providerType)) {
			throw new IllegalArgumentException("Unsupported provider: " + providerType);
		}
		return authServiceMap.get(providerType);
	}
}
