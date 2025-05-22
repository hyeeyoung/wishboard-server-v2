package com.wishboard.server.item.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.notifications.domain.model.Notifications;

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

	// Updated to accept notification details directly instead of a Notifications object
	public static ItemFolderNotificationDto of(Item item, ItemNotificationType itemNotificationType, LocalDateTime itemNotificationDate) {
		return ItemFolderNotificationDto.builder()
			.id(item.getId())
			.userId(item.getUser() == null ? null : item.getUser().getId())
			.folderId(item.getFolderId()) // Use the new getFolderId() method
			.folderName(null) // Needs to be populated by the service layer after fetching Folder by folderId
			.itemImages(item.getImages().stream().map(image -> new ItemImageDto(image.getItemImg(), image.getItemImageUrl())).toList())
			.itemMemo(item.getItemMemo())
			.itemName(item.getItemName())
			.itemPrice(item.getItemPrice())
			.itemUrl(item.getItemUrl())
			.itemNotificationType(itemNotificationType) // Directly from parameters
			.itemNotificationDate(itemNotificationDate) // Directly from parameters
			.readState(itemNotificationType == null ? null : Boolean.FALSE) // Default to false if a notification is expected
			.createdAt(item.getCreatedAt())
			.updatedAt(item.getUpdatedAt())
			.build();
	}
}
