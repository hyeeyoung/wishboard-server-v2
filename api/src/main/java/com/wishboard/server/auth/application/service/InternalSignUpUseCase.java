package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.application.dto.command.SignUpCommand;
import com.wishboard.server.common.domain.OsType;
import com.wishboard.server.common.util.PasswordEncoderPolicy;
import com.wishboard.server.user.application.service.support.UserValidator;
import com.wishboard.server.user.domain.model.AuthType;
import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class InternalSignUpUseCase {

	private final UserValidator userValidator;
	private final PasswordEncoderPolicy passwordEncoderPolicy;

	private final UserRepository userRepository;

	public Long execute(SignUpCommand signUpCommand, OsType osType, String deviceInfo) {
		userValidator.existsByEmailAndAuthType(signUpCommand.getEmail(), AuthType.INTERNAL);
		String hashedPassword = passwordEncoderPolicy.encode(signUpCommand.getPassword());
		User user = userRepository.save(
			User.newInstance(signUpCommand.getEmail(), hashedPassword, signUpCommand.getFcmToken(), deviceInfo, AuthType.INTERNAL, osType));
		return user.getId();
	}
}
