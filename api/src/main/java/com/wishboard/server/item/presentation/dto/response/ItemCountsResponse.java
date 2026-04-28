package com.wishboard.server.item.presentation.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCountsResponse {
	private long totalCount;
	private long ownedCount;
}
