package com.wishboard.server.domain.folder.repository;

import java.util.List;

import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;

public interface FolderRepositoryCustom {
	List<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId);
}
