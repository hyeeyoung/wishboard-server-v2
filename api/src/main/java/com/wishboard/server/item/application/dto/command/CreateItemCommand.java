package com.wishboard.server.item.application.dto.command;

import com.wishboard.server.common.domain.ItemNotificationType;

public record CreateItemCommand(
	Long folderId,
	String itemName,
	int itemPrice,
	String itemMemo,
	String itemUrl,
	ItemNotificationType itemNotificationType,
	String itemNotificationDate
) {
}
