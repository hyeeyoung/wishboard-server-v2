package com.wishboard.server.service.auth.impl;

import com.wishboard.server.common.util.HttpHeaderUtils;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.external.client.kakao.KakaoApiClient;
import com.wishboard.server.external.client.kakao.dto.response.KakaoProfileResponse;
import com.wishboard.server.service.auth.ExternalAuthService;
import com.wishboard.server.service.auth.dto.request.LoginDto;
import com.wishboard.server.service.user.UserService;
import com.wishboard.server.service.user.UserServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KaKaoAuthService implements ExternalAuthService {

    private static final UserProviderType socialType = UserProviderType.KAKAO;

    private final KakaoApiClient kaKaoApiCaller;

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Long login(LoginDto request) {
        KakaoProfileResponse response = kaKaoApiCaller.getProfileInfo(HttpHeaderUtils.withBearerToken(request.getToken())).block();
        User user = UserServiceUtils.findUserBySocialIdAndSocialType(userRepository, response.getId(), socialType);
        if (user == null)
            return userService.registerSocialUser(request.toCreateUserDto(response.getId()));
        return user.getId();
    }
}
