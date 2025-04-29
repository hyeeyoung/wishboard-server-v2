package com.wishboard.server.domain.folder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;

public interface FolderRepositoryCustom {
	Page<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable);
}
