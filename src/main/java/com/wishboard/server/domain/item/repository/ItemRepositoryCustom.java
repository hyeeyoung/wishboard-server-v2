package com.wishboard.server.domain.item.repository;

import java.util.List;

import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;

public interface ItemRepositoryCustom {
	List<ItemFolderNotificationDto> findAllByUserId(Long userId);
}
