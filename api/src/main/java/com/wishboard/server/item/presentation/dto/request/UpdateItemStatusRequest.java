package com.wishboard.server.item.presentation.dto.request;

import com.wishboard.server.item.domain.model.ItemStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateItemStatusRequest(
	@Schema(description = "아이템 상태", example = "OWNED")
	@NotNull(message = "아이템 상태는 필수입니다.")
	ItemStatus status
) {
}
