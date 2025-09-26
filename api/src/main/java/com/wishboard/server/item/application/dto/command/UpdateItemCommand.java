package com.wishboard.server.item.application.dto.command;

import com.wishboard.server.common.domain.ItemNotificationType;

public record UpdateItemCommand(
	Long folderId,
	String itemName,
	int itemPrice,
	String itemMemo,
	String itemUrl,
	ItemNotificationType itemNotificationType,
	String itemNotificationDate,
	Boolean imageChanged,
	Long version
) {
}
