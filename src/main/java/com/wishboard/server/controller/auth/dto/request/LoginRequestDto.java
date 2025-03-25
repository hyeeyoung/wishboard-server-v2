package com.wishboard.server.controller.auth.dto.request;

import com.wishboard.server.domain.user.UserProviderType;
import com.wishboard.server.service.auth.dto.request.LoginDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequestDto {

    @Schema(description = "소셜 로그인 타입 - KAKAO, APPLE", example = "KAKAO")
    @NotNull(message = "{user.socialType.notNull}")
    private UserProviderType socialType;

    @Schema(description = "토큰 - socialToken", example = "ijv4qLk0I7jYuDpFe-9A-oAx59-AAfC6UbTuairPCj1zTQAAAYI6e-6o")
    @NotBlank(message = "{auth.token.notBlank}")
    private String token;

    public LoginDto toServiceDto() {
        return LoginDto.of(socialType, token);
    }
}
