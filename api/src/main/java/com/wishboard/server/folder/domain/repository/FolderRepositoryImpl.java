package com.wishboard.server.folder.domain.repository;

import static com.wishboard.server.folder.domain.model.QFolder.*;
import static com.wishboard.server.item.domain.model.QItem.*;
import static com.wishboard.server.notifications.domain.model.QNotifications.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.item.application.dto.ItemFolderNotificationDto;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.QItem;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.model.QNotifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final NumberExpression<Integer> FOLDER_ORDER_NULL_RANK = new CaseBuilder()
		.when(folder.folderOrder.isNull()).then(1)
		.otherwise(0);
	private static final OrderSpecifier<?>[] FOLDER_ORDER_SPECIFIERS = new OrderSpecifier[] {
		new OrderSpecifier<>(Order.ASC, FOLDER_ORDER_NULL_RANK),
		folder.folderOrder.asc(),
		folder.createdAt.desc(),
		folder.id.desc()
	};
	private static final OrderSpecifier<?>[] LATEST_ORDER_SPECIFIERS = new OrderSpecifier[] {
		folder.createdAt.desc(),
		folder.id.desc()
	};

	@Override
	public Page<ItemFolderNotificationDto> findItemListByUserIdAndFolderId(Long userId, Long folderId, Pageable pageable) {
		Long totalElements = queryFactory
			.select(item.id.count())
			.from(item)
			.where(item.user.id.eq(userId),
				item.folder.id.eq(folderId)
			)
			.fetchOne();

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
		return new PageImpl<>(dtoList, pageable, totalElements == null ? 0L : totalElements);
	}

	@Override
	public Page<Folder> findAllByUserIdOrderByFolderOrder(Long userId, Pageable pageable) {
		Long totalElements = queryFactory
			.select(folder.id.count())
			.from(folder)
			.where(folder.user.id.eq(userId))
			.fetchOne();

		List<Folder> folders = queryFactory
			.selectFrom(folder)
			.where(folder.user.id.eq(userId))
			.orderBy(FOLDER_ORDER_SPECIFIERS)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(folders, pageable, totalElements == null ? 0L : totalElements);
	}

	@Override
	public List<Folder> findAllByUserIdOrderByFolderOrder(Long userId) {
		return queryFactory
			.selectFrom(folder)
			.where(folder.user.id.eq(userId))
			.orderBy(FOLDER_ORDER_SPECIFIERS)
			.fetch();
	}

	@Override
	public Page<Folder> findAllByUserIdOrderByLatest(Long userId, Pageable pageable) {
		Long totalElements = queryFactory
			.select(folder.id.count())
			.from(folder)
			.where(folder.user.id.eq(userId))
			.fetchOne();

		List<Folder> folders = queryFactory
			.selectFrom(folder)
			.where(folder.user.id.eq(userId))
			.orderBy(LATEST_ORDER_SPECIFIERS)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(folders, pageable, totalElements == null ? 0L : totalElements);
	}

	@Override
	public List<Folder> findAllByUserIdOrderByLatest(Long userId) {
		return queryFactory
			.selectFrom(folder)
			.where(folder.user.id.eq(userId))
			.orderBy(LATEST_ORDER_SPECIFIERS)
			.fetch();
	}

	@Override
	public List<Folder> findAllByUserIdOrderByRecentItem(Long userId) {
		return queryFactory
			.selectFrom(folder)
			.leftJoin(item).on(item.folder.id.eq(folder.id), item.user.id.eq(userId))
			.where(folder.user.id.eq(userId))
			.groupBy(folder.id)
			.orderBy(item.createdAt.max().coalesce(folder.createdAt).desc(), folder.id.desc())
			.fetch();
	}
}
