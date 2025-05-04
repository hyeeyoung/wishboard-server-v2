package com.wishboard.server.service.auth;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		UserServiceUtils.existsByEmailAndAuthType(userRepository, request.email(), AuthType.INTERNAL);
	}

	public Long signup(SignupRequest request, OsType osType, String deviceInfo) {
		UserServiceUtils.existsByEmailAndAuthType(userRepository, request.email(), AuthType.INTERNAL);
		String hashedPassword = AuthServiceUtils.getHashedPassword(request.password());
		User user = userRepository.save(
			User.newInstance(request.email(), hashedPassword, request.fcmToken(), deviceInfo, AuthType.INTERNAL, osType));
		return user.getId();
	}

	public User signIn(SigninRequest request, OsType osType, String deviceInfo) {
		User user = UserServiceUtils.findByEmailAndAuthType(userRepository, request.email(), AuthType.INTERNAL);
		boolean isPasswordMatch = encoder.matches(request.password(), user.getPassword());
		if (!isPasswordMatch) {
			throw new ValidationException("비밀번호가 일치하지 않습니다.", VALIDATION_PASSWORD_EXCEPTION);
		}
		// 현재 유저의 os 정보 갱신
		user.updateDeviceInformation(request.fcmToken(), osType, deviceInfo);
		return user;
	}

	public User reSignIn(ReSigninRequest request, OsType osType, String deviceInfo) {
		User user = UserServiceUtils.findByEmailAndAuthType(userRepository, request.email(), AuthType.INTERNAL);
		// 현재 유저의 os 정보 갱신
		user.updateDeviceInformation(request.fcmToken(), osType, deviceInfo);
		return user;
	}

	public String reSignInBeforeSendMail(ReSigninMailRequest request) {
		UserServiceUtils.findByEmailAndAuthType(userRepository, request.email(), AuthType.INTERNAL);
		String verificationCode = UuidUtils.generate().replace("-", "").substring(0, 6);
		mailClient.sendEmailWithVerificationCode(request.email(), verificationCode);
		return verificationCode;
	}

	public void logout(Long userId, String deviceInfo) {
		var user = UserServiceUtils.findUserById(userRepository, userId);
		var fcmTokens = user.getFcmTokens();
		// deviceInfo가 일치하는 토큰이 있으면 그거만 삭제. 없으면 전체 삭제
		boolean removed = fcmTokens.removeIf(token -> deviceInfo.equals(token.getFcmToken()));
		if (!removed) {
			fcmTokens.clear();
		}
		userRepository.save(user);
	}
}
