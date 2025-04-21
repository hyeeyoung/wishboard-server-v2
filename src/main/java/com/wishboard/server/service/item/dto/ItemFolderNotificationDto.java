package com.wishboard.server.service.item.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.notifications.ItemNotificationType;
import com.wishboard.server.domain.notifications.Notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFolderNotificationDto {
	private Long id;
	private Long userId;
	private Long folderId;
	private String folderName;
	private List<ItemImageDto> itemImages;
	private String itemMemo;
	private String itemName;
	private String itemPrice;
	private String itemUrl;
	private ItemNotificationType itemNotificationType;
	private LocalDateTime itemNotificationDate;
	private Boolean readState;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ItemFolderNotificationDto of(Item item, Notifications notifications) {
		return ItemFolderNotificationDto.builder()
			.id(item.getItemId())
			.userId(item.getUser() == null ? null : item.getUser().getId())
			.folderId(item.getFolder() == null ? null : item.getFolder().getId())
			.folderName(item.getFolder() == null ? null : item.getFolder().getFolderName())
			.itemImages(item.getImages().stream().map(image -> new ItemImageDto(image.getItemImg(), image.getItemImageUrl())).toList())
			.itemMemo(item.getItemMemo())
			.itemName(item.getItemName())
			.itemPrice(item.getItemPrice())
			.itemUrl(item.getItemUrl())
			.itemNotificationType(notifications == null ? null : notifications.getItemNotificationType())
			.itemNotificationDate(notifications == null ? null : notifications.getItemNotificationDate())
			.createdAt(item.getCreatedAt())
			.updatedAt(item.getUpdatedAt())
			.build();
	}
}
