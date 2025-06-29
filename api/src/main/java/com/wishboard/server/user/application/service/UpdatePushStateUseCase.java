package com.wishboard.server.user.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.user.application.dto.UserDto;
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdatePushStateUseCase {
	private final UserRepository userRepository;

	private final UserReader userReader;

	private final ModelMapper modelMapper;

	public UserDto execute(Long userId, boolean pushState) {
		var user = userReader.findById(userId);
		user.updatePushState(pushState);
		return modelMapper.map(user, UserDto.class);
	}
}
