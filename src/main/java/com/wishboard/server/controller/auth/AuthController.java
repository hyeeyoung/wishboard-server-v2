package com.wishboard.server.controller.auth;

import java.util.Optional;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.config.resolver.HeaderOsType;
import com.wishboard.server.controller.auth.dto.request.CheckEmailRequestDto;
import com.wishboard.server.controller.auth.dto.request.ReSigninMailRequestDto;
import com.wishboard.server.controller.auth.dto.request.ReSigninRequestDto;
import com.wishboard.server.controller.auth.dto.request.SigninRequestDto;
import com.wishboard.server.controller.auth.dto.request.SocialLoginRequestDto;
import com.wishboard.server.controller.auth.dto.request.SignupRequestDto;
import com.wishboard.server.controller.auth.dto.response.ReSigninMailResponseDto;
import com.wishboard.server.controller.auth.dto.response.SigninResponse;
import com.wishboard.server.controller.auth.dto.response.SocialLoginResponse;
import com.wishboard.server.controller.auth.dto.response.SignupResponse;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.service.auth.AuthService;
import com.wishboard.server.service.auth.ExternalAuthService;
import com.wishboard.server.service.auth.AuthServiceProvider;
import com.wishboard.server.service.auth.CreateTokenService;
import com.wishboard.server.service.auth.dto.request.TokenRequestDto;
import com.wishboard.server.service.auth.dto.response.TokenResponseDto;
import com.wishboard.server.service.user.UserServiceUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthServiceProvider authServiceProvider;
    private final CreateTokenService createTokenService;

    private final AuthService authService;

    @PostMapping("/v2/auth/login")
    @Override
    public SuccessResponse<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequestDto request) {
        ExternalAuthService externalAuthService = authServiceProvider.getAuthService(request.getSocialType());
        Long userId = externalAuthService.login(request.toServiceDto());
        TokenResponseDto tokenInfo = createTokenService.createTokenInfo(userId);
        return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, SocialLoginResponse.of(userId, tokenInfo));
    }

    @PostMapping("/v2/auth/refresh")
    @Override
    public SuccessResponse<TokenResponseDto> refreshToken(@Valid @RequestBody TokenRequestDto request) {
        return SuccessResponse.success(SuccessCode.REFRESH_TOKEN_SUCCESS, createTokenService.REFRESHToken(request));
    }

    @PostMapping("/v2/auth/signup")
    @Override
    public SuccessResponse<SignupResponse> signup(@Valid @RequestBody SignupRequestDto request, @HeaderOsType OsType osType) {
        Long userId = authService.signup(request, osType);
        TokenResponseDto token = createTokenService.createTokenInfo(userId);
        String temporaryNickname = UserServiceUtils.getRandomNickname();
        return SuccessResponse.success(SuccessCode.SIGNUP_SUCCESS, SignupResponse.of(token, temporaryNickname));
    }

    @PostMapping("/v2/auth/signin")
    @Override
    public SuccessResponse<SigninResponse> signin(@Valid @RequestBody SigninRequestDto request, @HeaderOsType OsType osType) {
        User user = authService.signin(request, osType);
        TokenResponseDto token = createTokenService.createTokenInfo(user.getId());
        String nickname = Optional.ofNullable(user.getNickname()).orElse(UserServiceUtils.getRandomNickname());
        return SuccessResponse.success(SuccessCode.SIGNIN_SUCCESS, SigninResponse.of(token, nickname));
    }

    @PostMapping("/v2/auth/check-email")
    @Override
    public SuccessResponse<Object> checkEmail(@Valid @RequestBody CheckEmailRequestDto request, @HeaderOsType OsType osType) {
        authService.checkEmail(request);
        return SuccessResponse.success(SuccessCode.CHECK_EMAIL_SUCCESS, null);
    }

    @PostMapping("/v2/auth/re-signin")
    @Override
    public SuccessResponse<SigninResponse> reSigninWithoutPassword(@Valid @RequestBody ReSigninRequestDto request, @HeaderOsType OsType osType) {
        User user = authService.reSignin(request, osType);
        TokenResponseDto token = createTokenService.createTokenInfo(user.getId());
        String nickname = Optional.ofNullable(user.getNickname()).orElse(UserServiceUtils.getRandomNickname());
        return SuccessResponse.success(SuccessCode.RESIGNIN_SUCCESS, SigninResponse.of(token, nickname));
    }

    @PostMapping("/v2/auth/password-mail")
    @Override
    public SuccessResponse<ReSigninMailResponseDto> reSigninBeforeSendMail(@Valid @RequestBody ReSigninMailRequestDto request, @HeaderOsType OsType osType) {
        String verificationCode = authService.reSigninBeforeSendMail(request);
        return SuccessResponse.success(SuccessCode.SEND_MAIL_SUCCESS, ReSigninMailResponseDto.builder().verificationCode(verificationCode).build());
    }
}
