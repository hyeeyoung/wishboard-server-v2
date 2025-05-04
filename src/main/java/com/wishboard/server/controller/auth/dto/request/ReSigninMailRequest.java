package com.wishboard.server.controller.auth.dto.request;

import com.wishboard.server.service.auth.dto.command.SignInCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReSigninMailRequest(
	@Schema(description = "email", example = "wishboard123@gmail.com")
	@NotBlank(message = "{auth.email.notBlank}")
	String email
) {
	public SignInCommand toCommand() {
		return SignInCommand.builder()
			.email(this.email)
			.build();
	}
}
