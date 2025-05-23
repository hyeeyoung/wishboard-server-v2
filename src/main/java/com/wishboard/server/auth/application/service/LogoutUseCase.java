package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class LogoutUseCase {

	private final UserRepository userRepository;
	private final UserReader userReader;

	public void execute(Long userId, String deviceInfo) {
		var user = userReader.findById(userId);
		var fcmTokens = user.getFcmTokens();
		// deviceInfo가 일치하는 토큰이 있으면 그거만 삭제. 없으면 전체 삭제
		boolean removed = fcmTokens.removeIf(token -> deviceInfo.equals(token.getFcmToken()));
		if (!removed) {
			fcmTokens.clear();
		}
		userRepository.save(user);
	}
}
