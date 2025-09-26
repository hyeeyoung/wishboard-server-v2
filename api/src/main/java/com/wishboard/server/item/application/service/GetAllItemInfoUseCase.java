package com.wishboard.server.item.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetAllItemInfoUseCase {
	private final UserReader userReader;

	private final ItemRepository itemRepository;

	public Page<ItemFolderNotificationDto> execute(Long userId, Pageable pageable) {
		var user = userReader.findById(userId);
		return itemRepository.findAllByUserId(user.getId(), pageable);
	}
}
