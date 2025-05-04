package com.wishboard.server.service.auth;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.util.UuidUtils;
import com.wishboard.server.domain.user.AuthType;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.domain.user.repository.UserRepository;
import com.wishboard.server.external.client.MailClient;
import com.wishboard.server.service.auth.dto.command.CheckEmailCommand;
import com.wishboard.server.service.auth.dto.command.SignInCommand;
import com.wishboard.server.service.auth.dto.command.SignUpCommand;
import com.wishboard.server.service.user.UserServiceUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

	private final MailClient mailClient;

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public void checkEmail(CheckEmailCommand checkEmailCommand) {
		UserServiceUtils.existsByEmailAndAuthType(userRepository, checkEmailCommand.email(), AuthType.INTERNAL);
	}

	public Long signup(SignUpCommand signUpCommand, OsType osType, String deviceInfo) {
		UserServiceUtils.existsByEmailAndAuthType(userRepository, signUpCommand.getEmail(), AuthType.INTERNAL);
		String hashedPassword = AuthServiceUtils.getHashedPassword(signUpCommand.getPassword());
		User user = userRepository.save(
			User.newInstance(signUpCommand.getEmail(), hashedPassword, signUpCommand.getFcmToken(), deviceInfo, AuthType.INTERNAL, osType));
		return user.getId();
	}

	public User signIn(SignInCommand signInCommand, OsType osType, String deviceInfo) {
		User user = UserServiceUtils.findByEmailAndAuthType(userRepository, signInCommand.getEmail(), AuthType.INTERNAL);
		boolean isPasswordMatch = encoder.matches(signInCommand.getPassword(), user.getPassword());
		if (!isPasswordMatch) {
			throw new ValidationException("비밀번호가 일치하지 않습니다.", VALIDATION_PASSWORD_EXCEPTION);
		}
		// 현재 유저의 os 정보 갱신
		user.updateDeviceInformation(signInCommand.getFcmToken(), osType, deviceInfo);
		return user;
	}

	public User reSignIn(SignInCommand signInCommand, OsType osType, String deviceInfo) {
		User user = UserServiceUtils.findByEmailAndAuthType(userRepository, signInCommand.getEmail(), AuthType.INTERNAL);
		// 현재 유저의 os 정보 갱신
		user.updateDeviceInformation(signInCommand.getFcmToken(), osType, deviceInfo);
		return user;
	}

	public String reSignInBeforeSendMail(SignInCommand signInCommand) {
		UserServiceUtils.findByEmailAndAuthType(userRepository, signInCommand.getEmail(), AuthType.INTERNAL);
		String verificationCode = UuidUtils.generate().replace("-", "").substring(0, 6);
		mailClient.sendEmailWithVerificationCode(signInCommand.getEmail(), verificationCode);
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
