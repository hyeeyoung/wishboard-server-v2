package com.wishboard.server.service.auth.dto.request;

import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.service.user.dto.CreateUserDto;

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
public class LoginDto {

	private UserProviderType socialType;
	private String token;
	private String fcmToken;

	public static LoginDto of(UserProviderType socialType, String token, String fcmToken) {
		return new LoginDto(socialType, token, fcmToken);
	}

	public CreateUserDto toCreateUserDto(String socialId) {
		return CreateUserDto.of(socialId, socialType);
	}
}
