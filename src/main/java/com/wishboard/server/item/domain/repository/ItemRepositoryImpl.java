package com.wishboard.server.item.domain.repository;

import static com.wishboard.server.folder.domain.model.QFolder.*;
import static com.wishboard.server.item.domain.model.QItem.*;
import static com.wishboard.server.item.domain.model.QItemImage.*;
import static com.wishboard.server.notifications.domain.model.QNotifications.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.item.application.dto.FolderItemDto;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.ItemImage;
import com.wishboard.server.item.domain.model.QItem;
import com.wishboard.server.item.domain.model.QItemImage;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.model.QNotifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ItemFolderNotificationDto> findAllByUserId(Long userId, Pageable pageable) {
		List<Tuple> results = queryFactory
			.selectDistinct(item, itemImage, folder, notifications)
			.from(item)
			.leftJoin(item.images, itemImage).fetchJoin()
			.leftJoin(folder).on(item.folderId.eq(folder.id)) 
			.leftJoin(notifications).on(item.id.eq(notifications.itemId).and(item.user.id.eq(notifications.userId))) // Ensure notification matches item and its user
			.where(item.user.id.eq(userId))
			.orderBy(item.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		List<ItemFolderNotificationDto> dtoList = results.stream()
			.map(tuple -> {
				Item item = tuple.get(QItem.item);
				// ItemImage itemImage = tuple.get(QItemImage.itemImage); // itemImage is fetched but not directly used in DTO construction here
				Notifications notifications = tuple.get(QNotifications.notifications); // This might be null due to leftJoin
				if (ObjectUtils.isEmpty(item)) {
					return new ItemFolderNotificationDto(); // Should ideally not happen if item is root and results exist
				}
				return ItemFolderNotificationDto.of(item, 
												    notifications != null ? notifications.getItemNotificationType() : null, 
												    notifications != null ? notifications.getItemNotificationDate() : null);
			}).toList();
		return new PageImpl<>(dtoList, pageable, results.size()); // Consider if results.size() is accurate for total elements if distinct is used
	}

	@Override
	public Map<Long, FolderItemDto> findLatestItemByFolderIds(List<Long> folderIds) {
		List<Item> items = queryFactory
			.selectFrom(item)
			.where(item.folderId.in(folderIds)) // Changed: item.folder.id.in(folderIds) to item.folderId.in(folderIds)
			.orderBy(item.folderId.asc(), item.createdAt.desc()) // Changed: item.folder.id.asc() to item.folderId.asc()
			.fetch();

		// createdAt 내림차순으로 정렬된 items에서 folderId를 키로 하여 첫 번째 아이템만 남김
		Map<Long, Item> lastItemImage = items.stream()
			.collect(Collectors.toMap(
				item -> item.getFolderId(), // Changed: item.getFolder().getId() to item.getFolderId()
				Function.identity(),
				(oldValue, newValue) -> oldValue) // createdAt 내림차순 → 첫 번째만 유지
			);

		// 폴더에 저장된 아이템 개수
		Map<Long, Long> itemCountMap = folderIds.stream()
			.collect(Collectors.toMap(
				folderId -> folderId,
				folderId -> items.stream().filter(item -> item.getFolderId().equals(folderId)).count() // Changed: item.getFolder().getId() to item.getFolderId()
			));

		return folderIds.stream()
			.collect(Collectors.toMap(
				Function.identity(),
				folderId -> {
					Item lastestItem = lastItemImage.get(folderId);
					Long count = itemCountMap.getOrDefault(folderId, 0L);

					if (ObjectUtils.isEmpty(lastestItem)) {
						return FolderItemDto.builder()
							.itemCount(0L)
							.lastestItem(null)
							.build();
					}

					return FolderItemDto.builder()
						.itemCount(count)
						.lastestItem(lastestItem)
						.build();
				}
			));
	}
}
