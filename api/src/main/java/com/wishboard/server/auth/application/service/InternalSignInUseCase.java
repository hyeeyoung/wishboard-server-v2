package com.wishboard.server.auth.application.service;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.application.dto.command.SignInCommand;
import com.wishboard.server.common.domain.OsType;
import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.util.PasswordEncoderPolicy;
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.model.AuthType;
import com.wishboard.server.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class InternalSignInUseCase {

	private final UserReader userReader;
	private final PasswordEncoderPolicy passwordEncoderPolicy;

	public User execute(SignInCommand signInCommand, OsType osType, String deviceInfo) {
		User user = userReader.findByEmailAndAuthType(signInCommand.getEmail(), AuthType.INTERNAL);
		boolean isPasswordMatch = passwordEncoderPolicy.matches(signInCommand.getPassword(), user.getPassword());
		if (!isPasswordMatch) {
			throw new ValidationException("비밀번호가 일치하지 않습니다.", VALIDATION_PASSWORD_EXCEPTION);
		}
		// 현재 유저의 os 정보 갱신
		user.updateDeviceInformation(signInCommand.getFcmToken(), osType, deviceInfo);
		return user;
	}
}
