package com.wishboard.server.item.application.service.support;

import static com.wishboard.server.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.wishboard.server.common.exception.ConflictException;
import com.wishboard.server.common.exception.NotFoundException;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemReader {
	private final ItemRepository itemRepository;

	public Item findById(Long itemId, Long userId) {
		Item item = itemRepository.findById(itemId)
			.orElseThrow(() -> new NotFoundException(
				String.format("아이템(%s) 을 찾을 수 없습니다", itemId), NOT_FOUND_ITEM_EXCEPTION));
		if (!item.getUser().getId().equals(userId)) {
			throw new ConflictException(String.format("다른 사용자의 아이템 입니다. (userId: %s, itemId: %s)", userId, itemId), CONFLICT_ITEM_EXCEPTION);
		}
		return item;
	}
}
