package com.wishboard.server.service.user.dto;

import com.wishboard.server.domain.user.UserProviderType;

import lombok.*;

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
