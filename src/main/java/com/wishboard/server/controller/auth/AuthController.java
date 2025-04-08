package com.wishboard.server.controller.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.resolver.HeaderOsType;
import com.wishboard.server.controller.auth.dto.request.SigninRequestDto;
import com.wishboard.server.controller.auth.dto.request.SocialLoginRequestDto;
import com.wishboard.server.controller.auth.dto.request.SignupRequestDto;
import com.wishboard.server.controller.auth.dto.response.SigninResponse;
import com.wishboard.server.controller.auth.dto.response.SocialLoginResponse;
import com.wishboard.server.controller.auth.dto.response.SignupResponse;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.service.auth.AuthService;
import com.wishboard.server.service.auth.ExternalAuthService;
import com.wishboard.server.service.auth.AuthServiceProvider;
import com.wishboard.server.service.auth.CreateTokenService;
import com.wishboard.server.service.auth.dto.request.TokenRequestDto;
import com.wishboard.server.service.auth.dto.response.TokenResponseDto;
import com.wishboard.server.service.user.UserServiceUtils;

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

    private final AuthService authService;

    @Operation(
        summary = "[X] 로그인 요청",
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
                    3. fcmToken 을 입력해주세요.
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다."),
        @ApiResponse(responseCode = "409", description = """
                    이미 해당 계정으로 회원가입하셨습니다.
                    로그인 해주세요.
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/v2/auth/login")
    public SuccessResponse<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequestDto request) {
        ExternalAuthService externalAuthService = authServiceProvider.getAuthService(request.getSocialType());
        Long userId = externalAuthService.login(request.toServiceDto());
        TokenResponseDto tokenInfo = createTokenService.createTokenInfo(userId);
        return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, SocialLoginResponse.of(userId, tokenInfo));
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
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "토큰이 만료되었습니다. 다시 로그인 해주세요.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v2/auth/refresh")
    public SuccessResponse<TokenResponseDto> reissue(@Valid @RequestBody TokenRequestDto request) {
        return SuccessResponse.success(SuccessCode.REISSUE_TOKEN_SUCCESS, createTokenService.reissueToken(request));
    }

    @Operation(
        summary = "wishboard 회원 가입",
        description = "wishboard 회원 가입을 진행합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공입니다."),
        @ApiResponse(responseCode = "400", description = """
                    1. @가 포함된 이메일 주소를 입력해주세요.  (email)
                    2. 비밀번호를 입력해주세요. (password)
                    3. fcmToken을 입력해주세요. (fcmToken)
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "이미 해당 계정으로 회원가입하셨습니다. 로그인 해주세요.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/v2/auth/signup")
    public SuccessResponse<SignupResponse> signup(@RequestBody SignupRequestDto request, @Parameter(hidden = true) @HeaderOsType OsType osType) {
        Long userId = authService.signup(request, osType);
        TokenResponseDto token = createTokenService.createTokenInfo(userId);
        String temporaryNickname = UserServiceUtils.getRandomNickname();
        return SuccessResponse.success(SuccessCode.SIGNUP_SUCCESS, SignupResponse.of(token, temporaryNickname));
    }

    @Operation(
        summary = "wishboard 회원 로그인",
        description = "wishboard 회원 로그인을 진행합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 로그인 성공입니다."),
        @ApiResponse(responseCode = "400", description = """
                    1. @가 포함된 이메일 주소를 입력해주세요. (email)
                    2. 비밀번호를 입력해주세요. (password)
                    3. fcmToken을 입력해주세요. (fcmToken)
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/v2/auth/signin")
    public SuccessResponse<SigninResponse> signin(@Valid @RequestBody SigninRequestDto request, @Parameter(hidden = true) @HeaderOsType OsType osType) {
        Long userId = authService.signin(request, osType);
        TokenResponseDto token = createTokenService.createTokenInfo(userId);
        String temporaryNickname = UserServiceUtils.getRandomNickname();
        return SuccessResponse.success(SuccessCode.SIGNIN_SUCCESS, SigninResponse.of(token, temporaryNickname));
    }
}
