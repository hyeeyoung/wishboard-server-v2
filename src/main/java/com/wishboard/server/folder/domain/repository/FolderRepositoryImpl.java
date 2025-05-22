package com.wishboard.server.folder.domain.repository;

import static com.wishboard.server.folder.domain.model.QFolder.*;
import static com.wishboard.server.item.domain.model.QItem.*;
import static com.wishboard.server.notifications.domain.model.QNotifications.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.QItem;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.model.QNotifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable) {
		List<Tuple> results = queryFactory
			.select(item, folder, notifications)
			.from(item)
			.leftJoin(folder).on(item.folderId.eq(folder.id)) // Changed to use folderId
			.leftJoin(notifications).on(item.id.eq(notifications.itemId).and(item.user.id.eq(notifications.userId))) // Changed to use itemId and userId
			.where(
				item.user.id.eq(userId),
				folder.id.eq(folderId)
			)
			.orderBy(item.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		List<ItemFolderNotificationDto> dtoList = results.stream()
			.map(tuple -> {
				Item item = tuple.get(QItem.item);
				Notifications notifications = tuple.get(QNotifications.notifications); // This might be null due to leftJoin
				if (ObjectUtils.isEmpty(item)) {
					// This case should ideally not happen if item is the root of the query and results are non-empty.
					// If it can, returning an empty DTO or handling it as an error might be options.
					// For now, assume 'item' will be present if 'results' has entries.
					return new ItemFolderNotificationDto(); 
				}
				// Pass notification details if notifications object is not null
				return ItemFolderNotificationDto.of(item, 
												    notifications != null ? notifications.getItemNotificationType() : null, 
												    notifications != null ? notifications.getItemNotificationDate() : null);
			}).toList();
		return new PageImpl<>(dtoList, pageable, results.size()); // Note: results.size() might not be the total count for pagination if distinct items are fewer than tuples.
	}
}
