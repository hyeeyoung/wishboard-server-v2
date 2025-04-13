package com.wishboard.server.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReSigninMailRequest {

    @Schema(description = "email", example = "wishboard123@gmail.com")
    @NotBlank(message = "{auth.email.notBlank")
    private String email;
}
