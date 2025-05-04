package com.wishboard.server.service.auth.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInCommand {
	private Boolean verify;
	private String email;
	private String password;
	private String fcmToken;
}
