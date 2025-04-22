package com.wishboard.server.service.item.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.domain.item.Item;

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
			.folderId(item.getFolder() == null ? null : item.getFolder().getId())
			.folderName(item.getFolder() == null ? null : item.getFolder().getFolderName())
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
