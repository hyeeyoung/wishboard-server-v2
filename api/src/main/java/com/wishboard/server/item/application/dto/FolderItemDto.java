package com.wishboard.server.item.application.dto;

import com.wishboard.server.item.domain.model.Item;

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
