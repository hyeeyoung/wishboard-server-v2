package com.wishboard.server.controller.item.request;

import com.wishboard.server.domain.notifications.ItemNotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequest {

	@Schema(description = "folderId", example = "1")
	private Long folderId;

	@Schema(description = "itemName", example = "나이키 V2K")
	@NotBlank(message = "{item.itemName.notBlank")
	private String itemName;

	@Schema(description = "itemPrice", example = "12300")
	private int itemPrice;

	@Schema(description = "itemMemo", example = "메모입니다.")
	private String itemMemo;

	@Schema(description = "itemUrl", example = "https://naver.com")
	private String itemUrl;

	@Schema(description = "알림 타입", example = "REMINDER")
	private ItemNotificationType itemNotificationType;

	@Schema(description = "알림 날짜", example = "2025-01-01 10:00:00")
	private String itemNotificationDate;
}
