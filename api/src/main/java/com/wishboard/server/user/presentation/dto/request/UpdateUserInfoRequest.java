package com.wishboard.server.user.presentation.dto.request;

import com.wishboard.server.user.application.dto.command.UpdateUserCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfoRequest(
	@Schema(description = "nickname", example = "김위시보드")
	@NotBlank(message = "{user.nickname.notBlank")
	@Max(value = 10, message = "{user.nickname.maxLength}")
	String nickname
) {
	public UpdateUserCommand toCommand() {
		return new UpdateUserCommand(this.nickname);
	}
}
