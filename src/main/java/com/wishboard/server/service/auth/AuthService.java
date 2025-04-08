package com.wishboard.server.service.auth;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.controller.auth.dto.request.SigninRequestDto;
import com.wishboard.server.controller.auth.dto.request.SignupRequestDto;
import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.service.user.UserServiceUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Long signup(SignupRequestDto request, OsType osType) {
        UserServiceUtils.existsByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);
        String hashedPassword = encoder.encode(request.getPassword());
        User user = userRepository.save(User.newInstance(request.getEmail(), hashedPassword, request.getFcmToken(), AuthType.INTERNAL, osType));
        return user.getId();
    }

    public Long signin(SigninRequestDto request, OsType osType) {
        User user = UserServiceUtils.findByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);
        boolean isPasswordMatch = encoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.");
        }

        // fcm 토큰이 다른 유저에 존재한다면, 다른 유저를 null 처리
        List<User> anotherUsers = userRepository.findByIdNotAndFcmTokenAndAuthType(user.getId(), request.getFcmToken(), AuthType.INTERNAL);
        if (!anotherUsers.isEmpty()) {
            anotherUsers.forEach(anotherUser -> anotherUser.updateDeviceInformation(null, null));
        }

        // 현재 유저의 fcm 토큰 및 os 정보 갱신
        user.updateDeviceInformation(request.getFcmToken(), osType);

        return user.getId();
    }
}
