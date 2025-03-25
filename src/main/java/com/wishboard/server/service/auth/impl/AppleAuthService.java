package com.wishboard.server.service.auth.impl;

import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.external.client.apple.AppleTokenProvider;
import com.wishboard.server.service.auth.AuthService;
import com.wishboard.server.service.auth.dto.request.LoginDto;
import com.wishboard.server.service.user.UserService;
import com.wishboard.server.service.user.UserServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppleAuthService implements AuthService {

    private static final UserProviderType socialType = UserProviderType.APPLE;

    private final AppleTokenProvider appleTokenDecoder;

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Long login(LoginDto request) {
        String socialId = appleTokenDecoder.getSocialIdFromIdToken(request.getToken());
        User user = UserServiceUtils.findUserBySocialIdAndSocialType(userRepository, socialId, socialType);
        if (user == null) return userService.registerUser(request.toCreateUserDto(socialId));
        return user.getId();
    }
}
