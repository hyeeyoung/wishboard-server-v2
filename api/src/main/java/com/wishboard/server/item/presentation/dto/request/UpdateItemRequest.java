package com.wishboard.server.item.presentation.dto.request;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.item.application.dto.command.UpdateItemCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateItemRequest(
	@Schema(description = "folderId", example = "1")
	Long folderId,

	@Schema(description = "itemName", example = "나이키 V2K")
	@NotBlank(message = "{item.itemName.notBlank}")
	@Size(max = 512, message = "{item.itemName.max")
	String itemName,

	@Schema(description = "itemPrice", example = "12300")
	@Max(value = Integer.MAX_VALUE, message = "{item.itemPrice.max}")
	int itemPrice,

	@Schema(description = "itemMemo", example = "메모입니다.")
	String itemMemo,

	@Schema(description = "itemUrl", example = "https://naver.com")
	@Size(max = 1024, message = "{item.itemUrl.max")
	String itemUrl,

	@Schema(description = "알림 타입", example = "REMINDER")
	ItemNotificationType itemNotificationType,

	@Schema(description = "알림 날짜", example = "2025-01-01 10:00:00")
	String itemNotificationDate,

	@Schema(description = "아이템 수정 시 기존 이미지 중 1개라도 변경된 경우 true로 전달", example = "true")
	@NotNull(message = "{item.imageChanged.notNull}")
	Boolean imageChanged,

	@Schema(description = "해당 item의 버전 (동시성 수정 방지를 위함으로 조회 시 받은 값을 그대로 보내주시면 됩니다.")
	@NotNull(message = "{item.version.notNull}")
	Long version
) {
	public UpdateItemCommand toCommand() {
		return new UpdateItemCommand(this.folderId, this.itemName, this.itemPrice, this.itemMemo, this.itemUrl, this.itemNotificationType,
			this.itemNotificationDate, this.imageChanged, this.version);
	}
}
