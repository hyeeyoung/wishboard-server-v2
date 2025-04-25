package com.wishboard.server.domain.item.repository;

import java.util.List;
import java.util.Map;

import com.wishboard.server.service.item.dto.FolderItemDto;
import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;

public interface ItemRepositoryCustom {
	List<ItemFolderNotificationDto> findAllByUserId(Long userId);

	Map<Long, FolderItemDto> findLatestItemByFolderIds(List<Long> folderIds);
}
