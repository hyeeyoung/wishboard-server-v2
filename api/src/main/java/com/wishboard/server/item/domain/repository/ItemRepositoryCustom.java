package com.wishboard.server.item.domain.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wishboard.server.item.application.dto.FolderItemDto;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.domain.model.ItemStatus;

public interface ItemRepositoryCustom {
	Page<ItemFolderNotificationDto> findAllByUserId(Long userId, Pageable pageable);

	Map<Long, FolderItemDto> findLatestItemByFolderIds(List<Long> folderIds);

	long countByUserId(Long userId);

	long countByUserIdAndStatus(Long userId, ItemStatus status);
}
