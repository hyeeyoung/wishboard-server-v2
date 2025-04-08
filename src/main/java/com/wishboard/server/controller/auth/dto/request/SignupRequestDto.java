package com.wishboard.server.controller.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    @Schema(description = "email", example = "wishboard123@gmail.com")
    @NotBlank(message = "{user.email.notBlank}")
    private String email;

    @Schema(description = "password", example = "qwer1234!")
    @NotBlank(message="{user.password.notBlank}")
    private String password;

    @Schema(description = "push 알림을 위한 fcm token", example = "ijv4qLk0I7jYuDpFe-9A-oAx59-AAfC6UbTuairPCj1zTQAAAYI6e-6o")
    @NotBlank(message="{user.fcmToken.notBlank}")
    private String fcmToken;
}
