package com.wishboard.server.item.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.item.domain.model.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFolderDto {
	private Long id;
	private Long userId;
	private Long folderId;
	private String folderName;
	private List<ItemImageDto> itemImages;
	private String itemMemo;
	private String itemName;
	private String itemPrice;
	private String itemUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ItemFolderDto of(Item item) {
		return ItemFolderDto.builder()
			.id(item.getId())
			.userId(item.getUser() == null ? null : item.getUser().getId())
			.folderId(item.getFolderId()) // Use the new getFolderId() method
			.folderName(null) // Needs to be populated by the service layer after fetching Folder by folderId
			.itemImages(item.getImages().stream().map(image -> new ItemImageDto(image.getItemImg(), image.getItemImageUrl())).toList())
			.itemMemo(item.getItemMemo())
			.itemName(item.getItemName())
			.itemPrice(item.getItemPrice())
			.itemUrl(item.getItemUrl())
			.createdAt(item.getCreatedAt())
			.updatedAt(item.getUpdatedAt())
			.build();
	}
}
