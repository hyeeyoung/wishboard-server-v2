package com.wishboard.server.controller.version;

import java.util.List;

import com.wishboard.server.common.dto.ErrorResponse;
import com.wishboard.server.common.dto.SuccessResponse;
import com.wishboard.server.controller.version.request.UpdateVersionRequest;
import com.wishboard.server.controller.version.response.VersionInfoResponse;
import com.wishboard.server.domain.user.OsType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Version", description = "버전 관리용 API")
public interface VersionControllerDocs {

	@Operation(summary = "OS별 버전 정보 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "버전 정보 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<VersionInfoResponse> getVersionByOs(@Parameter(hidden = true) OsType osType);

	@Operation(summary = "버전 정보 변경")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "버전 정보 수정 성공입니다."),
		@ApiResponse(responseCode = "400", description = """
				1. 최소 버전을 입력해주세요.
				2. 추천 버전을 입력해주세요.
				3. 허용하지 않는 User-Agent의 요청입니다.
			""", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<VersionInfoResponse> updateVersionByOs(@Parameter(hidden = true) OsType osType, UpdateVersionRequest request);

	@Operation(summary = "버전 정보 전체 조회 (서버 확인용)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "버전 리스트 조회 성공입니다."),
		@ApiResponse(responseCode = "400", description = "허용하지 않는 User-Agent의 요청입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "500", description = "예상치 못한 서버 에러가 발생하였습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
	})
	SuccessResponse<List<VersionInfoResponse>> getVersions(@Parameter(hidden = true) OsType osType);

}
