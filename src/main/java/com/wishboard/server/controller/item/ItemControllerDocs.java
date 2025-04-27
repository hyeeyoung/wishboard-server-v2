package com.wishboard.server.controller.item;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.config.swagger.SwaggerBody;
import com.wishboard.server.controller.item.request.CreateItemRequest;
import com.wishboard.server.controller.item.request.UpdateItemRequest;
import com.wishboard.server.controller.item.response.ItemInfoResponse;
import com.wishboard.server.domain.item.AddType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Item", description = "아이템 관련 API")
public interface ItemControllerDocs {

	@Operation(summary = "아이템 리스트 조회 (홈화면 조회)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "아이템 리스트 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<List<ItemInfoResponse>> getAllItemInfo(@Parameter(hidden = true) Long userId);

	@Operation(summary = "아이템 정보 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "아이템 정보 조회 성공입니다."),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "다른 사용자의 아이템입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<ItemInfoResponse> getItemInfo(@Parameter(hidden = true) Long userId, @Parameter(name = "itemId", example = "1") Long itemId);

	@Operation(summary = "아이템 생성")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = """
			1. 아이템 생성 성공입니다.
			2. 아이템 및 알림 생성 성공입니다.
			"""),
		@ApiResponse(responseCode = "400", description = """
			1. 아이템 이름을 입력해주세요.
			2. 아이템 등록 타입 (type)은 필수입니다.
			3. 알림 날짜는 현재 날짜보다 같거나 미래만 가능합니다.
			4. 알림 날짜는 30분 단위로만 등록 가능합니다. (00분 또는 30분)
			5. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = """
			1. 탈퇴했거나 존재하지 않는 유저입니다.
			2. 존재하지 않는 폴더입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@SwaggerBody(content = @Content(
		encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
	SuccessResponse<ItemInfoResponse> createItem(@Parameter(hidden = true) Long userId, CreateItemRequest request, List<MultipartFile> images,
		AddType addType);

	@Operation(summary = "아이템 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = """
			1. 아이템 수정 성공입니다.
			2. 아이템 및 알림 수정 성공입니다.
			"""),
		@ApiResponse(responseCode = "400", description = """
			1. 아이템 이름을 입력해주세요.
			2. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = """
			1. 탈퇴했거나 존재하지 않는 유저입니다.
			2. 존재하지 않는 아이템입니다.
			3. 존재하지 않는 폴더입니다.
			4. 존재하지 않는 알람입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@SwaggerBody(content = @Content(
		encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
	SuccessResponse<ItemInfoResponse> updateItem(@Parameter(hidden = true) Long userId, UpdateItemRequest request, List<MultipartFile> images,
		@Parameter(name = "itemId", example = "1") Long itemId);

	@Operation(summary = "아이템 삭제")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 삭제 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = """
			1. 탈퇴했거나 존재하지 않는 유저입니다.
			2. 존재하지 않는 아이템입니다."""
			, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<Object> deleteItem(@Parameter(hidden = true) Long userId, @Parameter(name = "itemId", example = "1") Long itemId);

	@Operation(summary = "아이템 폴더 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "아이템 폴더 수정 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "404", description = """
			1. 탈퇴했거나 존재하지 않는 유저입니다.
			2. 존재하지 않는 아이템입니다.
			3. 존재하지 않는 폴더입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "다른 사용자의 아이템입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<ItemInfoResponse> updateItemFolder(@Parameter(hidden = true) Long userId, @Parameter(name = "itemId", example = "1") Long itemId,
		@Parameter(name = "folderId", example = "1") Long folderId);

}
