package com.wishboard.server.folder.presentation.docs;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.ErrorResponseWithCode;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.config.resolver.UserId;
import com.wishboard.server.config.swagger.SwaggerPageable;
import com.wishboard.server.folder.presentation.dto.request.CreateFolderRequest;
import com.wishboard.server.folder.presentation.dto.request.UpdateFolderRequest;
import com.wishboard.server.folder.presentation.dto.response.FolderInfoWithoutItemCountResponse;
import com.wishboard.server.folder.presentation.dto.response.FolderListResponse;
import com.wishboard.server.item.presentation.dto.response.ItemInfoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Folder", description = "폴더 관련 API")
public interface FolderControllerDocs {

	@Operation(summary = "폴더 리스트 조회 (폴더탭 조회)", description = "정렬은 최신순으로 고정이므로 size와 page 만 전달해주세요.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "폴더 리스트 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. 허용하지 않는 User-Agent의 요청입니다.
			2. Request Header에 디바이스 정보가 없습니다.
		""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@SwaggerPageable
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<Page<FolderListResponse>> getFolderList(@Parameter(hidden = true) @UserId Long userId, Pageable pageable);

	@Operation(summary = "폴더 생성")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "폴더 생성 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
				1. 폴더 이름을 입력해주세요.
				2. 허용하지 않는 User-Agent의 요청입니다.
				3. 폴더 이름은 10자 이하로 입력해주세요.
				4. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "404", description = "탈퇴했거나 존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 폴더명입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<FolderInfoWithoutItemCountResponse> createFolder(@Parameter(hidden = true) @UserId Long userId,
		CreateFolderRequest createFolderRequest);

	@Operation(summary = "폴더명 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "폴더명 수정 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
				1. 폴더 이름을 입력해주세요.
				2. 허용하지 않는 User-Agent의 요청입니다.
				3. 폴더 이름은 10자 이하로 입력해주세요.
				4. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "404", description = """
				1. 탈퇴했거나 존재하지 않는 유저입니다.
				2. 존재하지 않는 폴더입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 폴더명입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<FolderInfoWithoutItemCountResponse> updateFolder(@Parameter(hidden = true) @UserId Long userId,
		@Parameter(name = "folderId", example = "1") Long folderId, UpdateFolderRequest updateFolderRequest);

	@Operation(summary = "폴더 삭제")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "폴더 삭제 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. 허용하지 않는 User-Agent의 요청입니다.
			2. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "404", description = """
				1.탈퇴했거나 존재하지 않는 유저입니다.
				2. 존재하지 않는 폴더입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	SuccessResponse<Object> deleteFolder(@Parameter(hidden = true) @UserId Long userId,
		@Parameter(name = "folderId", example = "1") Long folderId);

	@Operation(summary = "폴더 내 아이템 리스트 조회", description = "정렬은 최신순으로 고정이므로 size와 page 만 전달해주세요.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "폴더 내 아이템 리스트 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. 허용하지 않는 User-Agent의 요청입니다.
			2. Request Header에 디바이스 정보가 없습니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = """
				1. 토큰이 만료되었습니다. 다시 로그인 해주세요.
				2. 유효하지 않은 토큰입니다.
				3. 탈퇴했거나 존재하지 않는 유저입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponseWithCode.class))),
		@ApiResponse(responseCode = "404", description = """
				1.탈퇴했거나 존재하지 않는 유저입니다.
				2. 존재하지 않는 폴더입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	@Parameter(name = "Device-Info", description = "디바이스 식별용 UUID", example = "69b5207d-04a3-4f01-a0a2-cc61661a9411", in = ParameterIn.HEADER, required = true)
	@SwaggerPageable
	SuccessResponse<Page<ItemInfoResponse>> getItemListInFolder(@Parameter(hidden = true) @UserId Long userId,
		@Parameter(name = "folderId", example = "1") Long folderId, Pageable pageable);

	@Operation(summary = "폴더 리스트 조회 (아이템 상세 화면)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "폴더 리스트 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
			1. 허용하지 않는 User-Agent의 요청입니다.
			2. Request Header에 디바이스 정보가 없습니다.
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
	SuccessResponse<List<FolderInfoWithoutItemCountResponse>> getFolderListWithoutItemCount(@Parameter(hidden = true) @UserId Long userId);
}
