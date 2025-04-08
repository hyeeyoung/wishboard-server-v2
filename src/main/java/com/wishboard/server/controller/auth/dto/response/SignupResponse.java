package com.wishboard.server.controller.auth.dto.response;

import com.wishboard.server.service.auth.dto.response.TokenResponseDto;

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
public class SignupResponse {

    private TokenResponseDto token;
    private String temporaryNickname;

    public static SignupResponse of(TokenResponseDto token, String temporaryNickname) {
        return SignupResponse.builder()
                .token(token)
                .temporaryNickname(temporaryNickname)
                .build();
    }
}
