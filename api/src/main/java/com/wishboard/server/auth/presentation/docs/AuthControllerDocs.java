package com.wishboard.server.auth.presentation.docs;

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
import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.ErrorResponseWithCode;
import com.wishboard.server.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDocs {

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
			4. Request Header에 디바이스 정보가 없습니다.
			5. 허용하지 않는 User-Agent의 요청입니다.
			6. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다."),
		@ApiResponse(responseCode = "409", description = """
			이미 해당 계정으로 회원가입하셨습니다.
			로그인 해주세요.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<SocialLoginResponse> socialLogin(SocialLoginRequest request, @Parameter(hidden = true) HttpServletRequest servletRequest);

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
			3. Request Header에 디바이스 정보가 없습니다.
			4. 허용하지 않는 User-Agent의 요청입니다.
			5. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<TokenResponseDto> refreshToken(TokenRequest request, @Parameter(hidden = true) HttpServletRequest servletRequest);

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
			4. 필수적인 요청 값이 입력되지 않았습니다.
			5. Request Header에 디바이스 정보가 없습니다.
			6. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 해당 계정으로 회원가입하셨습니다. 로그인 해주세요.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<SignupResponse> signup(SignupRequest request, @Parameter(hidden = true) OsType osType,
		@Parameter(hidden = true) HttpServletRequest servletRequest);

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
			4. Request Header에 디바이스 정보가 없습니다.
			5. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = """
			1. 이미 해당 계정으로 회원가입하셨습니다. 로그인 해주세요.
			2. FCM 토큰은 최대 3개까지 등록할 수 있습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<SigninResponse> signin(SigninRequest request, @Parameter(hidden = true) OsType osType,
		@Parameter(hidden = true) HttpServletRequest servletRequest);

	@Operation(
		summary = "wishboard 회원 이메일 확인",
		description = """
			wishboard 회원 가입 전 이메일이 가입이 유효한지 확인합니다.
			이미 존재하는 이메일인 경우 409 예외를 반환합니다.
			존재하지 않는 이메일인 경우 200, null을 반환합니다.
			"""
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "wishboard 이메일 확인 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. @가 포함된 이메일 주소를 입력해주세요. (email)
			2. 허용하지 않는 User-Agent의 요청입니다.
			3. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 해당 계정으로 회원가입하셨습니다. 로그인 해주세요.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<Object> checkEmail(CheckEmailRequest request, @Parameter(hidden = true) OsType osType);

	@Operation(
		summary = "wishboard 이메일 인증을 통한 비밀번호 없이 로그인 (2)",
		description = "wishboard 회원가입 시 기입한 이메일로 인증을 통해 비밀번호 없이 로그인을 수행합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "wishboard 재로그인 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. @가 포함된 이메일 주소를 입력해주세요.
			2. 이메일 인증 여부를 입력해주세요.
			3. fcmToken을 입력해주세요.
			4. Request Header에 디바이스 정보가 없습니다.
			5. 허용하지 않는 User-Agent의 요청입니다.
			6. 검증되지 않았습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = """
			1. 이미 해당 계정으로 회원가입하셨습니다. 로그인 해주세요.
			2. FCM 토큰은 최대 3개까지 등록할 수 있습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<SigninResponse> reSigninWithoutPassword(ReSigninRequest request, @Parameter(hidden = true) OsType osType,
		@Parameter(hidden = true) HttpServletRequest servletRequest);

	@Operation(
		summary = "wishboard 이메일 인증을 통한 비밀번호 없이 로그인 (1)",
		description = "wishboard 회원가입 시 기입한 이메일로 인증 메일을 전송합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "wishboard 이메일 인증 메일 전송 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. @가 포함된 이메일 주소를 입력해주세요.
			2. 허용하지 않는 User-Agent의 요청입니다.
			3. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = """
			1. 이미 해당 계정으로 회원가입하셨습니다. 로그인 해주세요.
			2. FCM 토큰은 최대 3개까지 등록할 수 있습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<ReSigninMailResponse> reSigninBeforeSendMail(ReSigninMailRequest request, @Parameter(hidden = true) OsType osType);

	@Operation(summary = "로그아웃")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그아웃 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. Request Header에 디바이스 정보가 없습니다.
			2. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<Object> logout(@Parameter(hidden = true) Long userId, @Parameter(hidden = true) HttpServletRequest servletRequest);
}
