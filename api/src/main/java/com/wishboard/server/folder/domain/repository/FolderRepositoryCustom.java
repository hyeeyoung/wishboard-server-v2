package com.wishboard.server.folder.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;

public interface FolderRepositoryCustom {
	Page<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable);

	Page<Folder> findAllByUserIdOrderByFolderOrder(Long userId, Pageable pageable);

	List<Folder> findAllByUserIdOrderByFolderOrder(Long userId);

	Page<Folder> findAllByUserIdOrderByLatest(Long userId, Pageable pageable);

	List<Folder> findAllByUserIdOrderByLatest(Long userId);

	List<Folder> findAllByUserIdOrderByRecentItem(Long userId);
}
