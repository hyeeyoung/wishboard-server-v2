package com.wishboard.server.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReSigninRequest(
	@Schema(description = "verify", example = "true")
	@NotNull(message = "{auth.verify.notNull")
	Boolean verify,

	@Schema(description = "email", example = "wishboard123@gmail.com")
	@NotBlank(message = "{auth.email.notBlank}")
	String email,

	@Schema(description = "push 알림을 위한 fcm token", example = "ijv4qLk0I7jYuDpFe-9A-oAx59-AAfC6UbTuairPCj1zTQAAAYI6e-6o")
	@NotBlank(message = "{auth.fcmToken.notBlank}")
	String fcmToken
) {
}
