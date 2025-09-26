package com.wishboard.server.auth.application.service.support;

import org.springframework.stereotype.Service;

import com.wishboard.server.auth.application.dto.LoginDto;
import com.wishboard.server.auth.infrastructure.kakao.KakaoApiClient;
import com.wishboard.server.auth.infrastructure.kakao.dto.response.KakaoProfileResponse;
import com.wishboard.server.common.util.HttpHeaderUtils;
import com.wishboard.server.user.application.service.RegisterSocialUserUseCase;
import com.wishboard.server.user.application.service.UserServiceUtils;
import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.model.UserProviderType;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KaKaoAuthService implements ExternalAuthService {

	private static final UserProviderType socialType = UserProviderType.KAKAO;

	private final KakaoApiClient kaKaoApiCaller;

	private final RegisterSocialUserUseCase registerSocialUserUseCase;
	private final UserRepository userRepository;

	@Override
	public UserProviderType providerType() {
		return UserProviderType.KAKAO;
	}

	@Override
	public Long login(LoginDto request) {
		KakaoProfileResponse response = kaKaoApiCaller.getProfileInfo(HttpHeaderUtils.withBearerToken(request.getToken())).block();
		User user = UserServiceUtils.findUserBySocialIdAndSocialType(userRepository, response.getId(), socialType);
		if (user == null)
			return registerSocialUserUseCase.execute(request.toCreateUserDto(response.getId()));
		return user.getId();
	}
}
