package com.wishboard.server.service.user.dto;

import com.wishboard.server.domain.user.UserProviderType;

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
public class CreateUserDto {

	private String socialId;

	private UserProviderType socialType;

	public static CreateUserDto of(String socialId, UserProviderType socialType) {
		return new CreateUserDto(socialId, socialType);
	}
}
