package com.wishboard.server.service.notifications.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.notifications.ItemNotificationType;
import com.wishboard.server.domain.notifications.Notifications;
import com.wishboard.server.service.item.dto.ItemImageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemNotificationDto {
	private Long id;
	private String itemUrl;
	private String itemName;
	private List<ItemImageDto> itemImages;
	private ItemNotificationType itemNotificationType;
	private LocalDateTime itemNotificationDate;
	private boolean readState;

	public static ItemNotificationDto of(Item item, Notifications notifications) {
		return ItemNotificationDto.builder()
			.id(item.getId())
			.itemUrl(item.getItemUrl())
			.itemName(item.getItemName())
			.itemImages(item.getImages().stream().map(image -> new ItemImageDto(image.getItemImg(), image.getItemImageUrl())).toList())
			.itemNotificationType(notifications.getItemNotificationType())
			.itemNotificationDate(notifications.getItemNotificationDate())
			.readState(notifications.getReadState())
			.build();
	}
}
