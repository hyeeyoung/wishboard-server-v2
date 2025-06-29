package com.wishboard.server.user.application.service.support;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.user.domain.model.AuthType;
import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.model.UserProviderType;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserReader {
	private final UserRepository userRepository;

	public User findById(Long userId) {
		return userRepository.findUserById(userId)
			.orElseThrow(() -> new NotFoundException(
				String.format("존재하지 않는 유저 (%s) 입니다", userId), NOT_FOUND_USER_EXCEPTION));
	}

	public User findByEmailAndAuthType(String email, AuthType authType) {
		return userRepository.findByEmailAndAuthType(email, authType)
			.orElseThrow(() -> new NotFoundException(
				String.format("이메일(%s)과 인증 유형(%s)에 해당하는 사용자를 찾을 수 없습니다.", email, authType), NOT_FOUND_USER_EXCEPTION));
	}

	public User findBySocialIdAndSocialType(String socialId, UserProviderType socialType) {
		return userRepository.findUserBySocialIdAndSocialType(socialId, socialType);
	}
}
