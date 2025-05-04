package com.wishboard.server.controller.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfoRequest(
	@Schema(description = "nickname", example = "김위시보드")
	@NotBlank(message = "{user.nickname.notBlank")
	String nickname
) {
}
