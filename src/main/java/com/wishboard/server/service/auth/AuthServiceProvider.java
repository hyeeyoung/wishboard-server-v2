package com.wishboard.server.service.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.service.auth.impl.AppleAuthService;
import com.wishboard.server.service.auth.impl.KaKaoAuthService;

@RequiredArgsConstructor
@Component
public class AuthServiceProvider {

    private static final Map<UserProviderType, AuthService> authServiceMap = new HashMap<>();

    private final AppleAuthService appleAuthService;
    private final KaKaoAuthService kaKaoAuthService;

    @PostConstruct
    void initializeAuthServicesMap() {
        authServiceMap.put(UserProviderType.APPLE, appleAuthService);
        authServiceMap.put(UserProviderType.KAKAO, kaKaoAuthService);
    }

    public AuthService getAuthService(UserProviderType socialType) {
        return authServiceMap.get(socialType);
    }
}
