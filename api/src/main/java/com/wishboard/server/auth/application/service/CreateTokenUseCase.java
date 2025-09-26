package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.infrastructure.jwt.JwtClient;
import com.wishboard.server.auth.presentation.dto.response.TokenResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateTokenUseCase {

	private final JwtClient jwtClient;

	public TokenResponseDto execute(Long userId, String deviceInfo) {
		return jwtClient.createTokenInfo(userId, deviceInfo);
	}
}
