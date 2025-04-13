package com.wishboard.server.controller.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class ReSigninMailResponse {
    private String verificationCode;
}
