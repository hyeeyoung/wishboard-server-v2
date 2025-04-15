package com.wishboard.server.controller.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

	@Schema(description = "newPassword", example = "qwer1234!!!")
	@NotBlank(message = "{user.newPassword.notBlank")
	private String newPassword;
}
