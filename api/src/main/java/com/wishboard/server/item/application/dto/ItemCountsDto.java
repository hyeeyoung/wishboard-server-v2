package com.wishboard.server.item.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemCountsDto {
	private long totalCount;
	private long ownedCount;
}
