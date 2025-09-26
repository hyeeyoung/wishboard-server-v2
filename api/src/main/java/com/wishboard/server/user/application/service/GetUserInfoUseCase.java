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
@Transactional(readOnly = true)
public class GetUserInfoUseCase {
	private final UserRepository userRepository;

	private final UserReader userReader;

	private final ModelMapper modelMapper;

	public UserDto execute(Long userId) {
		var user = userReader.findById(userId);
		return modelMapper.map(user, UserDto.class);
	}

}
