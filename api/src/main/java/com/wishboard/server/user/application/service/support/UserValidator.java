package com.wishboard.server.user.application.service.support;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.wishboard.server.common.exception.ConflictException;
import com.wishboard.server.user.domain.model.AuthType;
import com.wishboard.server.user.domain.model.UserProviderType;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {
	private final UserRepository userRepository;

	public void validateEmailUnique(String email, AuthType authType) {
		if (userRepository.existsByEmailAndAuthType(email, authType)) {
			throw new ConflictException(String.format("이미 존재하는 유저 (authType: %s, email: %s)", authType.getValue(), email),
				CONFLICT_USER_EXCEPTION);
		}
	}

	public void validateSocialUserNotExists(String socialId, UserProviderType socialType) {
		if (userRepository.existsBySocialIdAndSocialType(socialId, socialType)) {
			throw new ConflictException(String.format("이미 존재하는 유저 (%s - %s)", socialId, socialType),
				CONFLICT_USER_EXCEPTION);
		}
	}

	public void existsByEmailAndAuthType(String email, AuthType authType) {
		if (userRepository.existsByEmailAndAuthType(email, authType)) {
			throw new ConflictException(String.format("이미 존재하는 유저 (authType: %s, email: %s) 입니다", authType.getValue(), email),
				CONFLICT_USER_EXCEPTION);
		}
	}

	public void validateNicknameUnique(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new ConflictException(String.format("이미 존재하는 유저 닉네임 (%s) 입니다.", nickname), CONFLICT_USER_NICKNAME_EXCEPTION);
		}
	}
}
