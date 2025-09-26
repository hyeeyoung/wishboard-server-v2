package com.wishboard.server.auth.application.service.support;

import org.springframework.stereotype.Service;

import com.wishboard.server.auth.application.dto.LoginDto;
import com.wishboard.server.auth.infrastructure.apple.AppleTokenProvider;
import com.wishboard.server.user.application.service.RegisterSocialUserUseCase;
import com.wishboard.server.user.application.service.UserServiceUtils;
import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.model.UserProviderType;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AppleAuthService implements ExternalAuthService {

	private static final UserProviderType socialType = UserProviderType.APPLE;

	private final AppleTokenProvider appleTokenDecoder;

	private final RegisterSocialUserUseCase registerSocialUserUseCase;
	private final UserRepository userRepository;

	@Override
	public UserProviderType providerType() {
		return UserProviderType.APPLE;
	}

	@Override
	public Long login(LoginDto request) {
		String socialId = appleTokenDecoder.getSocialIdFromIdToken(request.getToken());
		User user = UserServiceUtils.findUserBySocialIdAndSocialType(userRepository, socialId, socialType);
		if (user == null)
			return registerSocialUserUseCase.execute(request.toCreateUserDto(socialId));
		return user.getId();
	}
}
