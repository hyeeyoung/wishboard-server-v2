package com.wishboard.server.service.auth;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.ConflictException;
import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.util.UuidUtils;
import com.wishboard.server.controller.auth.dto.request.CheckEmailRequest;
import com.wishboard.server.controller.auth.dto.request.ReSigninMailRequest;
import com.wishboard.server.controller.auth.dto.request.ReSigninRequest;
import com.wishboard.server.controller.auth.dto.request.SigninRequest;
import com.wishboard.server.controller.auth.dto.request.SignupRequest;
import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.external.client.MailClient;
import com.wishboard.server.service.user.UserServiceUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final MailClient mailClient;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public void checkEmail(CheckEmailRequest request) {
        UserServiceUtils.existsByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);
    }

    public Long signup(SignupRequest request, OsType osType) {
        UserServiceUtils.existsByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);
        String hashedPassword = AuthServiceUtils.getHashedPassword(request.getPassword());
        User user = userRepository.save(User.newInstance(request.getEmail(), hashedPassword, request.getFcmToken(), AuthType.INTERNAL, osType));
        return user.getId();
    }

    public User signin(SigninRequest request, OsType osType) {
        User user = UserServiceUtils.findByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);
        boolean isPasswordMatch = encoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new ValidationException("비밀번호가 일치하지 않습니다.", VALIDATION_PASSWORD_EXCEPTION);
        }

        if (user.getFcmTokens().size() >= 3) {
            throw new ConflictException("FCM 토큰은 최대 3개까지 등록할 수 있습니다.", CONFLICT_USER_FCM_TOKEN_EXCEPTION);
        }

        // 현재 유저의 os 정보 갱신
        user.updateDeviceInformation(request.getFcmToken(), osType);

        return user;
    }

    public User reSignin(ReSigninRequest request, OsType osType) {
        User user = UserServiceUtils.findByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);

        if (user.getFcmTokens().size() >= 3) {
            throw new ConflictException("FCM 토큰은 최대 3개까지 등록할 수 있습니다.", CONFLICT_USER_FCM_TOKEN_EXCEPTION);
        }

        // 현재 유저의 os 정보 갱신
        user.updateDeviceInformation(request.getFcmToken(), osType);

        return user;
    }

    public String reSigninBeforeSendMail(ReSigninMailRequest request) {
        UserServiceUtils.findByEmailAndAuthType(userRepository, request.getEmail(), AuthType.INTERNAL);
        String verificationCode = UuidUtils.generate().replace("-", "").substring(0, 6);
        mailClient.sendEmailWithVerificationCode(request.getEmail(), verificationCode);
        return verificationCode;
    }
}
