package com.wishboard.server.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.controller.auth.dto.request.LoginRequestDto;
import com.wishboard.server.controller.auth.dto.response.LoginResponse;
import com.wishboard.server.service.auth.AuthService;
import com.wishboard.server.service.auth.AuthServiceProvider;
import com.wishboard.server.service.auth.CreateTokenService;
import com.wishboard.server.service.auth.dto.request.TokenRequestDto;
import com.wishboard.server.service.auth.dto.response.TokenResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceProvider authServiceProvider;
    private final CreateTokenService createTokenService;

    @Operation(
        summary = "로그인 요청",
        description = """
                    카카오 로그인, 애플 로그인을 요청합니다.
                    최초 로그인의 경우 회원가입 처리 후 로그인됩니다.
                    socialType - KAKAO (카카오), APPLE (애플)
                    """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공입니다."),
        @ApiResponse(responseCode = "400", description = """
                    1. 유저의 socialType 를 입력해주세요.
                    2. access token 을 입력해주세요.
                    """),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다."),
        @ApiResponse(responseCode = "409", description = """
                    이미 해당 계정으로 회원가입하셨습니다.
                    로그인 해주세요.
                    """),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.")
    })
    @PostMapping("/v1/auth/login")
    public SuccessResponse<LoginResponse> login(@Valid @RequestBody LoginRequestDto request) {
        AuthService authService = authServiceProvider.getAuthService(request.getSocialType());
        Long userId = authService.login(request.toServiceDto());

        TokenResponseDto tokenInfo = createTokenService.createTokenInfo(userId);
        return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, LoginResponse.of(userId, tokenInfo));
    }

    @Operation(
        summary = "JWT Access Token 갱신",
        description = """
                    만료된 Access Token을 Refresh Token으로 갱신합니다.
                    Refresh Token이 유효하지 않거나 만료된 경우 갱신에 실패합니다.
                    """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공입니다."),
        @ApiResponse(responseCode = "400", description = """
                    1. access token 을 입력해주세요.
                    2. refresh token 을 입력해주세요.
                    """),
        @ApiResponse(responseCode = "401", description = "토큰이 만료되었습니다. 다시 로그인 해주세요."),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.")
    })
    @PostMapping("/v1/auth/refresh")
    public SuccessResponse<TokenResponseDto> reissue(@Valid @RequestBody TokenRequestDto request) {
        return SuccessResponse.success(SuccessCode.REISSUE_TOKEN_SUCCESS, createTokenService.reissueToken(request));
    }
}
