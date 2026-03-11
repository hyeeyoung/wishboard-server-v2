package com.wishboard.server.folder.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateFolderOrderRequest(
	@Schema(description = "위에서 아래 순서대로 정렬된 폴더 ID 목록", example = "[5,2,8,1]")
	@NotEmpty(message = "폴더 순서는 최소 1개 이상이어야 합니다.")
	List<@NotNull Long> folderIds
) {
}
