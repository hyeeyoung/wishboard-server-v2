package com.wishboard.server.controller.notifications.response;

import java.time.LocalDateTime;
import java.util.List;

import com.wishboard.server.domain.notifications.ItemNotificationType;
import com.wishboard.server.service.item.dto.ItemImageDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemNotificationResponse {
	private Long id;
	private String itemUrl;
	private List<ItemImageDto> itemImages;
	private String itemName;
	private ItemNotificationType itemNotificationType;
	private LocalDateTime itemNotificationDate;
	private boolean readState;
}
