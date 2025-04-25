package com.wishboard.server.service.item.dto;

import com.wishboard.server.domain.item.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderItemDto {
	private Long itemCount;
	private Item lastestItem;
}
