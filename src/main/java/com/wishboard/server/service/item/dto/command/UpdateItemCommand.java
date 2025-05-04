package com.wishboard.server.service.item.dto.command;

import com.wishboard.server.domain.notifications.ItemNotificationType;

public record UpdateItemCommand(
	Long folderId,
	String itemName,
	int itemPrice,
	String itemMemo,
	String itemUrl,
	ItemNotificationType itemNotificationType,
	String itemNotificationDate
) {
}
