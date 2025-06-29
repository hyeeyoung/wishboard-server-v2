package com.wishboard.server.auth.presentation.dto.request;

import com.wishboard.server.auth.application.dto.command.CheckEmailCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CheckEmailRequest(
	@Schema(description = "email", example = "wishboard123@gmail.com")
	@NotBlank(message = "{auth.email.notBlank}")
	String email
) {
	public CheckEmailCommand toCommand() {
		return new CheckEmailCommand(this.email);
	}
}
