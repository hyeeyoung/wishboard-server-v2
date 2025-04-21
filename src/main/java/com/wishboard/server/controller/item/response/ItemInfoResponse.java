package com.wishboard.server.controller.item.response;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.domain.notifications.ItemNotificationType;
import com.wishboard.server.service.item.dto.ItemImageDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemInfoResponse {
	private Long id;
	private Long userId;
	private Long folderId;
	private String folderName;
	private List<ItemImageDto> itemImages;
	private String itemName;
	private String itemPrice;
	private String itemUrl;
	private String itemMemo;
	private ItemNotificationType itemNotificationType;
	private LocalDateTime itemNotificationDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
