package com.wishboard.server.user.presentation.docs;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.config.swagger.SwaggerBody;
import com.wishboard.server.user.presentation.dto.request.UpdatePasswordRequest;
import com.wishboard.server.user.presentation.dto.request.UpdateUserInfoRequest;
import com.wishboard.server.user.presentation.dto.response.UserInfoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserControllerDocs {

	@Operation(summary = "사용자 정보 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<UserInfoResponse> getUserInfo(@Parameter(hidden = true) Long userId);

	@Operation(summary = "푸쉬 알림 설정 변경")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "푸쉬 알림 설정 변경 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<UserInfoResponse> updatePushState(@Parameter(hidden = true) Long userId,
		@Parameter(name = "pushState", example = "true") boolean pushState);

	@Operation(summary = "사용자 정보 변경", description = """
		v2 로 변경되면서 아래 사항이 수정되었으니 참고 부탁드립니다.
		- 사용자 정보 수정 시 이미지가 존재하는 경우, 기존 이미지를 모두 삭제하고, request 로 전달주신 이미지를 다시 저장하는 형태로 변경했습니다.
		- 기존에는 닉네임만 수정하는 경우 -> 닉네임만 전달하는 형태였지만, v2 에서는 닉네임과 이미지 모두 전달해주세요.
	""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 정보 변경 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. 닉네임을 입력해주세요.
			2. 허용하지 않는 User-Agent의 요청입니다.
			3. 닉네임은 10자 이하로 입력해주세요.
			4. 이미지 최대 크기는 720x720 입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@SwaggerBody(content = @Content(
		encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
	SuccessResponse<UserInfoResponse> updateUserInfo(@Parameter(hidden = true) Long userId, UpdateUserInfoRequest request, MultipartFile images);

	@Operation(summary = "사용자 wishboard 비밀번호 변경")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 wishboard 비밀번호 변경 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. 새 비밀번호를 입력해주세요.
			2. 필수적인 요청 값이 입력되지 않았습니다.
			3. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<UserInfoResponse> updatePassword(@Parameter(hidden = true) Long userId, UpdatePasswordRequest request);

	@Operation(summary = "사용자 탈퇴")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 탈퇴 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<Object> deleteUser(@Parameter(hidden = true) Long userId);
}
