package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.infrastructure.jwt.JwtClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpireTokenUseCase {

	private final JwtClient jwtClient;

	public void execute(Long userId, String deviceInfoFromHeader) {
		jwtClient.expireRefreshToken(userId, deviceInfoFromHeader);
	}
}
