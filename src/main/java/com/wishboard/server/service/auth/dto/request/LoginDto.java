package com.wishboard.server.service.auth.dto.request;

import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.service.user.dto.request.CreateUserDto;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginDto {

    private UserProviderType socialType;

    private String token;

    public static LoginDto of(UserProviderType socialType, String token) {
        return new LoginDto(socialType, token);
    }

    public CreateUserDto toCreateUserDto(String socialId) {
        return CreateUserDto.of(socialId, socialType);
    }
}
