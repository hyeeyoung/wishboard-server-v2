package com.wishboard.server.common.success;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.wishboard.server.common.success.SuccessStatusCode.OK;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    /**
     * 200 OK
     */
    SUCCESS(OK, "성공입니다."),

    // 인증
    LOGIN_SUCCESS(OK, "로그인 성공입니다."),
    REFRESH_TOKEN_SUCCESS(OK, "토큰 갱신 성공입니다."),
    
    CHECK_EMAIL_SUCCESS(OK, "wishboard 이메일 확인 성공입니다."),
    SIGNUP_SUCCESS(OK, "wishboard 회원가입 후 로그인 성공입니다."),
    SIGNIN_SUCCESS(OK, "wishboard 로그인 성공입니다."),
    RESIGNIN_SUCCESS(OK, "wishboard 재로그인 성공입니다."),
    SEND_MAIL_SUCCESS(OK, " 이메일 인증 메일 전송 성공입니다."),

    /**
     * 201 CREATED
     */

    /**
     * 202 ACCEPTED
     */

    /**
     * 204 NO_CONTENT
     */
    ;

    private final SuccessStatusCode statusCode;
    private final String message;

    public int getStatus() {
        return statusCode.getStatus();
    }
}
