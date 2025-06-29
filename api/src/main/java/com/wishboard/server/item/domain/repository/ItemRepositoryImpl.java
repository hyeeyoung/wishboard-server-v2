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
			.leftJoin(folder).on(item.folder.eq(folder))
			.leftJoin(notifications).on(item.eq(notifications.notificationId.item))
			.where(item.user.id.eq(userId))
			.orderBy(item.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		List<ItemFolderNotificationDto> dtoList = results.stream()
			.map(tuple -> {
				Item item = tuple.get(QItem.item);
				ItemImage itemImage = tuple.get(QItemImage.itemImage);
				Notifications notifications = tuple.get(QNotifications.notifications);
				if (ObjectUtils.isEmpty(item)) {
					return new ItemFolderNotificationDto();
				}
				return ItemFolderNotificationDto.of(item, notifications);
			}).toList();
		return new PageImpl<>(dtoList, pageable, results.size());
	}

	@Override
	public Map<Long, FolderItemDto> findLatestItemByFolderIds(List<Long> folderIds) {
		List<Item> items = queryFactory
			.selectFrom(item)
			.where(item.folder.id.in(folderIds))
			.orderBy(item.folder.id.asc(), item.createdAt.desc())
			.fetch();

		// createdAt 내림차순으로 정렬된 items에서 folderId를 키로 하여 첫 번째 아이템만 남김
		Map<Long, Item> lastItemImage = items.stream()
			.collect(Collectors.toMap(
				item -> item.getFolder().getId(),
				Function.identity(),
				(oldValue, newValue) -> oldValue) // createdAt 내림차순 → 첫 번째만 유지
			);

		// 폴더에 저장된 아이템 개수
		Map<Long, Long> itemCountMap = folderIds.stream()
			.collect(Collectors.toMap(
				folderId -> folderId,
				folderId -> items.stream().filter(item -> item.getFolder().getId().equals(folderId)).count()
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
