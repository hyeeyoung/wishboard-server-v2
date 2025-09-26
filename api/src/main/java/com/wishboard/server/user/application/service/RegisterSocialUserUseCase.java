package com.wishboard.server.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.user.application.dto.CreateUserDto;
import com.wishboard.server.user.domain.model.AuthType;
import com.wishboard.server.user.domain.model.User;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class RegisterSocialUserUseCase {
	private final UserRepository userRepository;

	public Long execute(CreateUserDto request) {
		UserServiceUtils.validateNotExistsUser(userRepository, request.getSocialId(), request.getSocialType());
		User user = userRepository.save(
			User.newInstance(request.getSocialId(), AuthType.SOCIAL, request.getSocialType()));
		return user.getId();
	}
}
