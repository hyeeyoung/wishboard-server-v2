package com.wishboard.server.domain.folder.repository;

import static com.wishboard.server.domain.folder.QFolder.*;
import static com.wishboard.server.domain.item.QItem.*;
import static com.wishboard.server.domain.notifications.QNotifications.*;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.item.QItem;
import com.wishboard.server.domain.notifications.Notifications;
import com.wishboard.server.domain.notifications.QNotifications;
import com.wishboard.server.service.item.dto.ItemFolderNotificationDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId) {
		List<Tuple> results = queryFactory
			.select(item, folder, notifications)
			.from(item)
			.leftJoin(folder).on(item.folder.eq(folder))
			.leftJoin(notifications).on(item.eq(notifications.notificationId.item))
			.where(
				item.user.id.eq(userId),
				folder.id.eq(folderId)
			)
			.orderBy(item.createdAt.desc())
			.fetch();
		return results.stream()
			.map(tuple -> {
				Item item = tuple.get(QItem.item);
				Notifications notifications = tuple.get(QNotifications.notifications);
				if (ObjectUtils.isEmpty(item)) {
					return new ItemFolderNotificationDto();
				}
				return ItemFolderNotificationDto.of(item, notifications);
			}).toList();
	}
}
