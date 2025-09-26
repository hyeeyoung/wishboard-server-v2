package com.wishboard.server.folder.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;

public interface FolderRepositoryCustom {
	Page<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable);
}
