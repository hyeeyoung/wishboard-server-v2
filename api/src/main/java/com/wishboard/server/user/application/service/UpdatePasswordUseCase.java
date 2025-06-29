package com.wishboard.server.user.application.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.common.util.PasswordEncoderPolicy;
import com.wishboard.server.user.application.dto.UserDto;
import com.wishboard.server.user.application.dto.command.UpdatePasswordCommand;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UpdatePasswordUseCase {
	private final UserReader userReader;
	private final PasswordEncoderPolicy passwordEncoderPolicy;

	private final ModelMapper modelMapper;

	public UserDto execute(Long userId, UpdatePasswordCommand updatePasswordCommand) {
		var user = userReader.findById(userId);
		String hashedPassword = passwordEncoderPolicy.encode(updatePasswordCommand.newPassword());
		user.updatePassword(hashedPassword);
		return modelMapper.map(user, UserDto.class);
	}
}
