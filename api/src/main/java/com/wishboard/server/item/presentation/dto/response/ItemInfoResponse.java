package com.wishboard.server.item.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.item.application.dto.ItemImageDto;
import com.wishboard.server.item.domain.model.ItemStatus;

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
	private ItemStatus itemStatus;
	private ItemNotificationType itemNotificationType;
	private LocalDateTime itemNotificationDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long version;
}
