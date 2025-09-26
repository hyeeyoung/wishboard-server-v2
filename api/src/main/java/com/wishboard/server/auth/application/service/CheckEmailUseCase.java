package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.application.dto.command.CheckEmailCommand;
import com.wishboard.server.user.application.service.support.UserValidator;
import com.wishboard.server.user.domain.model.AuthType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CheckEmailUseCase {
	private final UserValidator userValidator;

	public void execute(CheckEmailCommand checkEmailCommand) {
		userValidator.existsByEmailAndAuthType(checkEmailCommand.email(), AuthType.INTERNAL);
	}
}
