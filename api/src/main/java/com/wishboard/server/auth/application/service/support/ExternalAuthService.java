package com.wishboard.server.auth.application.service.support;

import com.wishboard.server.auth.application.dto.LoginDto;
import com.wishboard.server.user.domain.model.UserProviderType;

public interface ExternalAuthService {

	UserProviderType providerType();

	Long login(LoginDto request);
}
