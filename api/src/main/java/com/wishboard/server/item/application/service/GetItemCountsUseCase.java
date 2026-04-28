package com.wishboard.server.item.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wishboard.server.item.application.dto.ItemCountsDto;
import com.wishboard.server.item.domain.model.ItemStatus;
import com.wishboard.server.item.domain.repository.ItemRepository;
import com.wishboard.server.user.application.service.support.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetItemCountsUseCase {
	private final UserReader userReader;

	private final ItemRepository itemRepository;

	public ItemCountsDto execute(Long userId) {
		var user = userReader.findById(userId);
		long totalCount = itemRepository.countByUserId(user.getId());
		long ownedCount = itemRepository.countByUserIdAndStatus(user.getId(), ItemStatus.OWNED);
		return new ItemCountsDto(totalCount, ownedCount);
	}
}
