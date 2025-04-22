package com.wishboard.server.controller.notifications;

import java.util.List;

import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.controller.notifications.response.ItemNotificationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notifications", description = "알림 관련 API")
public interface NotificationsControllerDocs {

	@Operation(summary = "알림 내역 조회 (알림탭 조회)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "알림 내역 조회 성공입니다."),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<List<ItemNotificationResponse>> getAllNotiInfo(@Parameter(hidden = true) @UserId Long userId);

	@Operation(summary = "알림 확인")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "아이템 읽음 상태 수정 성공입니다."),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = """
				1. 탈퇴했거나 존재하지 않는 유저입니다.
				2. 존재하지 않는 아이템입니다.
				3. 존재하지 않는 알람입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<Object> updateNotificationsState(@Parameter(hidden = true) @UserId Long userId,
		@Parameter(name = "itemId", example = "1") Long itemId);

	@Operation(summary = "알림 캘린더 확인")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "알림 전체 조회 성공입니다."),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<List<ItemNotificationResponse>> getNotificationsCalendar(@Parameter(hidden = true) @UserId Long userId);
}
