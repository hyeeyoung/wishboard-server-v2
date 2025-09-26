package com.wishboard.server.auth.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.auth.application.dto.command.SignInCommand;
import com.wishboard.server.auth.infrastructure.mail.MailClient;
import com.wishboard.server.common.util.UuidUtils;
import com.wishboard.server.user.application.service.support.UserReader;
import com.wishboard.server.user.domain.model.AuthType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class InternalReSignInMailUseCase {

	private final UserReader userReader;

	private final MailClient mailClient;

	public String execute(SignInCommand signInCommand) {
		userReader.findByEmailAndAuthType(signInCommand.getEmail(), AuthType.INTERNAL);
		String verificationCode = UuidUtils.generate().replace("-", "").substring(0, 6);
		mailClient.sendEmailWithVerificationCode(signInCommand.getEmail(), verificationCode);
		return verificationCode;
	}
}
