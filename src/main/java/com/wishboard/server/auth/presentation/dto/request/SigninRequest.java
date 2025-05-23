package com.wishboard.server.auth.presentation.dto.request;

import com.wishboard.server.auth.application.dto.command.SignInCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SigninRequest(
	@Schema(description = "email", example = "wishboard123@gmail.com")
	@NotBlank(message = "{auth.email.notBlank}")
	String email,

	@Schema(description = "password", example = "qwer1234!")
	@NotBlank(message = "{auth.password.notBlank}")
	String password,

	@Schema(description = "push 알림을 위한 fcm token", example = "ijv4qLk0I7jYuDpFe-9A-oAx59-AAfC6UbTuairPCj1zTQAAAYI6e-6o")
	@NotBlank(message = "{auth.fcmToken.notBlank}")
	String fcmToken
) {
	public SignInCommand toCommand() {
		return SignInCommand.builder()
			.email(this.email)
			.password(this.password)
			.fcmToken(this.fcmToken).build();
	}
}
