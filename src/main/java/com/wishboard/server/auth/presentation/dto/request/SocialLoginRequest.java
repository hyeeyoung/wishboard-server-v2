package com.wishboard.server.auth.presentation.dto.request;

import com.wishboard.server.auth.application.dto.LoginDto;
import com.wishboard.server.user.domain.model.UserProviderType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// TODO 현재는 사용하지 않음.  추후 구현

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialLoginRequest {

	@Schema(description = "소셜 로그인 타입 - KAKAO, APPLE", example = "KAKAO")
	@NotNull(message = "{auth.socialType.notNull}")
	private UserProviderType socialType;

	@Schema(description = "토큰 - socialToken", example = "ijv4qLk0I7jYuDpFe-9A-oAx59-AAfC6UbTuairPCj1zTQAAAYI6e-6o")
	@NotBlank(message = "{auth.token.notBlank}")
	private String token;

	@Schema(description = "토큰 - fcmToken", example = "dfdafjdslkfjslfjslifsjvmdsklvdosijsmvsdjvosadjvosd")
	@NotBlank(message = "{auth.fcmToken.notBlank}")
	private String fcmToken;

	public LoginDto toServiceDto() {
		return LoginDto.of(socialType, token, fcmToken);
	}
}
