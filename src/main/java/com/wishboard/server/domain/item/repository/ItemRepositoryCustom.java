package com.wishboard.server.domain.item.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wishboard.server.service.item.dto.FolderItemDto;
import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;

public interface ItemRepositoryCustom {
	Page<ItemFolderNotificationDto> findAllByUserId(Long userId, Pageable pageable);

	Map<Long, FolderItemDto> findLatestItemByFolderIds(List<Long> folderIds);
}
