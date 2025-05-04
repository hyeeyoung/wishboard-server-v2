package com.wishboard.server.item.presentation.dto.request;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.item.application.dto.command.UpdateItemCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateItemRequest(
	@Schema(description = "folderId", example = "1")
	Long folderId,

	@Schema(description = "itemName", example = "나이키 V2K")
	@NotBlank(message = "{item.itemName.notBlank}")
	String itemName,

	@Schema(description = "itemPrice", example = "12300")
	int itemPrice,

	@Schema(description = "itemMemo", example = "메모입니다.")
	String itemMemo,

	@Schema(description = "itemUrl", example = "https://naver.com")
	String itemUrl,

	@Schema(description = "알림 타입", example = "REMINDER")
	ItemNotificationType itemNotificationType,

	@Schema(description = "알림 날짜", example = "2025-01-01 10:00:00")
	String itemNotificationDate
) {
	public UpdateItemCommand toCommand() {
		return new UpdateItemCommand(this.folderId, this.itemName, this.itemPrice, this.itemMemo, this.itemUrl, this.itemNotificationType,
			this.itemNotificationDate);
	}
}
