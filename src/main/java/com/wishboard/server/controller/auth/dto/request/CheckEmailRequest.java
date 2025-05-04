package com.wishboard.server.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CheckEmailRequest(
	@Schema(description = "email", example = "wishboard123@gmail.com")
	@NotBlank(message = "{auth.email.notBlank}")
	String email
) {
}
