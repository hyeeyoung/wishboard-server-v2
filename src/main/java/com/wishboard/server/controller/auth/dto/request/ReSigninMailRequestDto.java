package com.wishboard.server.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "wishboard 재로그인(이메일 인증 전) dto")
public class ReSigninMailRequestDto {

    @Schema(description = "email", example = "wishboard123@gmail.com")
    @NotBlank(message = "{auth.email.notBlank")
    private String email;
}
