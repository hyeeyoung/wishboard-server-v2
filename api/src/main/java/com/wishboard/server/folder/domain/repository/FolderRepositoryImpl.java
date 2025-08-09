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
import com.wishboard.server.item.domain.model.QItemImage;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.model.QNotifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable) {
		long totalElements = queryFactory
			.select(item.id.count())
			.from(item)
			.where(item.user.id.eq(userId),
				item.folder.id.eq(folderId)
			)
			.fetchOne();

		List<Tuple> results = queryFactory
			.select(item, folder, notifications)
			.from(item)
			.leftJoin(item.images, QItemImage.itemImage).fetchJoin()
			.leftJoin(folder).on(item.folder.eq(folder))
			.leftJoin(notifications).on(item.eq(notifications.notificationId.item))
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
				Notifications notifications = tuple.get(QNotifications.notifications);
				if (ObjectUtils.isEmpty(item)) {
					return new ItemFolderNotificationDto();
				}
				return ItemFolderNotificationDto.of(item, notifications);
			}).toList();
		return new PageImpl<>(dtoList, pageable, totalElements);
	}
}
