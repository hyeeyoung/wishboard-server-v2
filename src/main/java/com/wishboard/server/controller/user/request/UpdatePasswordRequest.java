package com.wishboard.server.controller.user.request;

import com.wishboard.server.service.user.dto.command.UpdatePasswordCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
	@Schema(description = "newPassword", example = "qwer1234!!!")
	@NotBlank(message = "{user.newPassword.notBlank")
	String newPassword
) {
	public UpdatePasswordCommand toCommand() {
		return new UpdatePasswordCommand(newPassword);
	}
}
