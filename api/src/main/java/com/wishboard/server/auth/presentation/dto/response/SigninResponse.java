package com.wishboard.server.auth.presentation.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SigninResponse {

	private TokenResponseDto token;
	private String temporaryNickname;

	public static SigninResponse of(TokenResponseDto token, String temporaryNickname) {
		return SigninResponse.builder()
			.token(token)
			.temporaryNickname(temporaryNickname)
			.build();
	}
}
