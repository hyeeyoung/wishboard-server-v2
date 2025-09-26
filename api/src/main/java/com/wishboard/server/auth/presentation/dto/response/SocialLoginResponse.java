package com.wishboard.server.auth.presentation.dto.response;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SocialLoginResponse {

	private Long userId;
	private TokenResponseDto token;

	public static SocialLoginResponse of(Long userId, TokenResponseDto token) {
		return SocialLoginResponse.builder()
			.userId(userId)
			.token(token)
			.build();
	}
}
