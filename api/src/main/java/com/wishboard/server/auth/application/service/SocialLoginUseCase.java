package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.application.dto.LoginDto;
import com.wishboard.server.auth.application.service.support.SocialServiceProvider;

import lombok.RequiredArgsConstructor;

// TODO 현재는 사용하지 않음.  추후 구현

@RequiredArgsConstructor
@Service
@Transactional
public class SocialLoginUseCase {
	private final SocialServiceProvider socialServiceProvider;

	public Long execute(LoginDto loginDto) {
		var externalAuthService = socialServiceProvider.get(loginDto.getSocialType());
		return externalAuthService.login(loginDto);
	}
}
