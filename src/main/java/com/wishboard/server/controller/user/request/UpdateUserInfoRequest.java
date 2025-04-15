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
public class UpdateUserInfoRequest {

	@Schema(description = "nickname", example = "김위시보드")
	@NotBlank(message = "{user.nickname.notBlank")
	private String nickname;
}
