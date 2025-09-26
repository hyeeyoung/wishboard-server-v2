package com.wishboard.server.auth.presentation;

import static com.wishboard.server.common.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wishboard.server.auth.application.service.CheckEmailUseCase;
import com.wishboard.server.auth.application.service.CreateTokenUseCase;
import com.wishboard.server.auth.application.service.ExpireTokenUseCase;
import com.wishboard.server.auth.application.service.GetRefreshTokenUseCase;
import com.wishboard.server.auth.application.service.InternalReSignInMailUseCase;
import com.wishboard.server.auth.application.service.InternalReSignInUseCase;
import com.wishboard.server.auth.application.service.InternalSignInUseCase;
import com.wishboard.server.auth.application.service.InternalSignUpUseCase;
import com.wishboard.server.auth.application.service.LogoutUseCase;
import com.wishboard.server.auth.application.service.SocialLoginUseCase;
import com.wishboard.server.auth.application.service.support.NicknameGenerator;
import com.wishboard.server.auth.presentation.docs.AuthControllerDocs;
import com.wishboard.server.auth.presentation.dto.request.CheckEmailRequest;
import com.wishboard.server.auth.presentation.dto.request.ReSigninMailRequest;
import com.wishboard.server.auth.presentation.dto.request.ReSigninRequest;
import com.wishboard.server.auth.presentation.dto.request.SigninRequest;
import com.wishboard.server.auth.presentation.dto.request.SignupRequest;
import com.wishboard.server.auth.presentation.dto.request.SocialLoginRequest;
import com.wishboard.server.auth.presentation.dto.request.TokenRequest;
import com.wishboard.server.auth.presentation.dto.response.ReSigninMailResponse;
import com.wishboard.server.auth.presentation.dto.response.SigninResponse;
import com.wishboard.server.auth.presentation.dto.response.SignupResponse;
import com.wishboard.server.auth.presentation.dto.response.SocialLoginResponse;
import com.wishboard.server.auth.presentation.dto.response.TokenResponseDto;
import com.wishboard.server.common.domain.OsType;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.common.exception.ValidationException;
import com.wishboard.server.common.success.SuccessCode;
import com.wishboard.server.common.util.HttpHeaderUtils;
import com.wishboard.server.config.interceptor.Auth;
import com.wishboard.server.config.resolver.HeaderOsType;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.user.domain.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
	private final SocialLoginUseCase socialLoginUseCase;

	private final CreateTokenUseCase createTokenUseCase;
	private final GetRefreshTokenUseCase getRefreshTokenUseCase;
	private final ExpireTokenUseCase expireTokenUseCase;

	private final InternalSignUpUseCase internalSignUpUseCase;
	private final InternalSignInUseCase internalSignInUseCase;
	private final InternalReSignInUseCase internalReSignInUseCase;
	private final InternalReSignInMailUseCase internalReSignInMailUseCase;
	private final CheckEmailUseCase checkEmailUseCase;

	private final LogoutUseCase logoutUseCase;

	private final NicknameGenerator nicknameGenerator;

	// TODO 현재는 사용하지 않음.  추후 구현
	@PostMapping("/v2/auth/login")
	@Override
	public SuccessResponse<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request, HttpServletRequest servletRequest) {
		Long userId = socialLoginUseCase.execute(request.toServiceDto());
		TokenResponseDto tokenInfo = createTokenUseCase.execute(userId, HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest));
		return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, SocialLoginResponse.of(userId, tokenInfo));
	}

	@PostMapping("/v2/auth/refresh")
	@Override
	public SuccessResponse<TokenResponseDto> refreshToken(@Valid @RequestBody TokenRequest request, HttpServletRequest servletRequest) {
		return SuccessResponse.success(SuccessCode.REFRESH_TOKEN_SUCCESS,
			getRefreshTokenUseCase.execute(request.toCommand(), HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest)));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/v2/auth/signup")
	@Override
	public SuccessResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request, @HeaderOsType OsType osType,
		HttpServletRequest servletRequest) {
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		Long userId = internalSignUpUseCase.execute(request.toCommand(), osType, deviceInfo);
		TokenResponseDto token = createTokenUseCase.execute(userId, deviceInfo);
		String temporaryNickname = nicknameGenerator.generate();
		return SuccessResponse.success(SuccessCode.SIGNUP_SUCCESS, SignupResponse.of(token, temporaryNickname));
	}

	@PostMapping("/v2/auth/signin")
	@Override
	public SuccessResponse<SigninResponse> signin(@Valid @RequestBody SigninRequest request, @HeaderOsType OsType osType,
		HttpServletRequest servletRequest) {
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		User user = internalSignInUseCase.execute(request.toCommand(), osType, deviceInfo);
		TokenResponseDto token = createTokenUseCase.execute(user.getId(), HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest));
		String nickname = Optional.ofNullable(user.getNickname()).orElse(nicknameGenerator.generate());
		return SuccessResponse.success(SuccessCode.SIGNIN_SUCCESS, SigninResponse.of(token, nickname));
	}

	@PostMapping("/v2/auth/check-email")
	@Override
	public SuccessResponse<Object> checkEmail(@Valid @RequestBody CheckEmailRequest request, @HeaderOsType OsType osType) {
		checkEmailUseCase.execute(request.toCommand());
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
		User user = internalReSignInUseCase.execute(request.toCommand(), osType, deviceInfo);
		TokenResponseDto token = createTokenUseCase.execute(user.getId(), HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest));
		String nickname = Optional.ofNullable(user.getNickname()).orElse(nicknameGenerator.generate());
		return SuccessResponse.success(SuccessCode.RESIGNIN_SUCCESS, SigninResponse.of(token, nickname));
	}

	@PostMapping("/v2/auth/password-mail")
	@Override
	public SuccessResponse<ReSigninMailResponse> reSigninBeforeSendMail(@Valid @RequestBody ReSigninMailRequest request,
		@HeaderOsType OsType osType) {
		String verificationCode = internalReSignInMailUseCase.execute(request.toCommand());
		return SuccessResponse.success(SuccessCode.SEND_MAIL_SUCCESS, ReSigninMailResponse.builder().verificationCode(verificationCode).build());
	}

	@Auth
	@PostMapping("/v2/auth/logout")
	@Override
	public SuccessResponse<Object> logout(@UserId Long userId, HttpServletRequest servletRequest) {
		String deviceInfo = HttpHeaderUtils.getDeviceInfoFromHeader(servletRequest);
		expireTokenUseCase.execute(userId, deviceInfo);
		logoutUseCase.execute(userId, deviceInfo);
		return SuccessResponse.success(SuccessCode.LOGOUT_SUCCESS, null);
	}
}
