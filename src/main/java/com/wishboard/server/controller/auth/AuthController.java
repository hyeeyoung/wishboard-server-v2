package com.wishboard.server.controller.auth;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.common.util.HttpHeaderUtils;
import com.wishboard.server.config.interceptor.Auth;
import com.wishboard.server.config.resolver.HeaderOsType;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.controller.auth.dto.request.CheckEmailRequest;
import com.wishboard.server.controller.auth.dto.request.ReSigninMailRequest;
import com.wishboard.server.controller.auth.dto.request.ReSigninRequest;
import com.wishboard.server.controller.auth.dto.request.SigninRequest;
import com.wishboard.server.controller.auth.dto.request.SignupRequest;
import com.wishboard.server.controller.auth.dto.request.SocialLoginRequest;
import com.wishboard.server.controller.auth.dto.response.ReSigninMailResponse;
import com.wishboard.server.controller.auth.dto.response.SigninResponse;
import com.wishboard.server.controller.auth.dto.response.SignupResponse;
import com.wishboard.server.controller.auth.dto.response.SocialLoginResponse;
import com.wishboard.server.domain.user.OsType;
import com.wishboard.server.domain.user.User;
import com.wishboard.server.service.auth.AuthService;
import com.wishboard.server.service.auth.AuthServiceProvider;
import com.wishboard.server.service.auth.ExternalAuthService;
import com.wishboard.server.service.auth.TokenService;
import com.wishboard.server.service.auth.dto.request.TokenRequest;
import com.wishboard.server.service.auth.dto.response.TokenResponseDto;
import com.wishboard.server.service.user.UserServiceUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

	private final AuthServiceProvider authServiceProvider;
	private final TokenService tokenService;

	private final AuthService authService;
	private final ModelMapper modelMapper;

	@PostMapping("/v2/auth/login")
	@Override
	public SuccessResponse<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request, HttpServletRequest servletRequest) {
		ExternalAuthService externalAuthService = authServiceProvider.getAuthService(request.getSocialType());
		Long userId = externalAuthService.login(request.toServiceDto());
		TokenResponseDto tokenInfo = tokenService.createTokenInfo(userId, HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest));
		return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, SocialLoginResponse.of(userId, tokenInfo));
	}

	@PostMapping("/v2/auth/refresh")
	@Override
	public SuccessResponse<TokenResponseDto> refreshToken(@Valid @RequestBody TokenRequest request, HttpServletRequest servletRequest) {
		return SuccessResponse.success(SuccessCode.REFRESH_TOKEN_SUCCESS,
			tokenService.getRefreshToken(request.toCommand(), HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest)));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/v2/auth/signup")
	@Override
	public SuccessResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request, @HeaderOsType OsType osType,
		HttpServletRequest servletRequest) {
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		Long userId = authService.signup(request.toCommand(), osType, deviceInfo);
		TokenResponseDto token = tokenService.createTokenInfo(userId, deviceInfo);
		String temporaryNickname = UserServiceUtils.getRandomNickname();
		return SuccessResponse.success(SuccessCode.SIGNUP_SUCCESS, SignupResponse.of(token, temporaryNickname));
	}

	@PostMapping("/v2/auth/signin")
	@Override
	public SuccessResponse<SigninResponse> signin(@Valid @RequestBody SigninRequest request, @HeaderOsType OsType osType,
		HttpServletRequest servletRequest) {
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		User user = authService.signIn(request.toCommand(), osType, deviceInfo);
		TokenResponseDto token = tokenService.createTokenInfo(user.getId(), HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest));
		String nickname = Optional.ofNullable(user.getNickname()).orElse(UserServiceUtils.getRandomNickname());
		return SuccessResponse.success(SuccessCode.SIGNIN_SUCCESS, SigninResponse.of(token, nickname));
	}

	@PostMapping("/v2/auth/check-email")
	@Override
	public SuccessResponse<Object> checkEmail(@Valid @RequestBody CheckEmailRequest request, @HeaderOsType OsType osType) {
		authService.checkEmail(request.toCommand());
		return SuccessResponse.success(SuccessCode.CHECK_EMAIL_SUCCESS, null);
	}

	@PostMapping("/v2/auth/re-signin")
	@Override
	public SuccessResponse<SigninResponse> reSigninWithoutPassword(@Valid @RequestBody ReSigninRequest request, @HeaderOsType OsType osType,
		HttpServletRequest servletRequest) {
		if (request.isVerify()) {
			throw new ValidationException("verify는 true여야 합니다.", VALIDATION_RE_SIGNIN_VERIFY_EXCEPTION);
		}
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		User user = authService.reSignIn(request.toCommand(), osType, deviceInfo);
		TokenResponseDto token = tokenService.createTokenInfo(user.getId(), HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest));
		String nickname = Optional.ofNullable(user.getNickname()).orElse(UserServiceUtils.getRandomNickname());
		return SuccessResponse.success(SuccessCode.RESIGNIN_SUCCESS, SigninResponse.of(token, nickname));
	}

	@PostMapping("/v2/auth/password-mail")
	@Override
	public SuccessResponse<ReSigninMailResponse> reSigninBeforeSendMail(@Valid @RequestBody ReSigninMailRequest request,
		@HeaderOsType OsType osType) {
		String verificationCode = authService.reSignInBeforeSendMail(request.toCommand());
		return SuccessResponse.success(SuccessCode.SEND_MAIL_SUCCESS, ReSigninMailResponse.builder().verificationCode(verificationCode).build());
	}

	@Auth
	@PostMapping("/v2/auth/logout")
	@Override
	public SuccessResponse<Object> logout(@UserId Long userId, HttpServletRequest servletRequest) {
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		tokenService.expireToken(userId, deviceInfo);
		authService.logout(userId, deviceInfo);
		return SuccessResponse.success(SuccessCode.LOGOUT_SUCCESS, null);

	}
}
