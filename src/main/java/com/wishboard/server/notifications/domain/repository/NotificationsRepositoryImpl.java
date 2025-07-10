package com.wishboard.server.notifications.domain.repository;

import static com.wishboard.server.item.domain.model.QItem.*;
import static com.wishboard.server.notifications.domain.model.QNotifications.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.item.domain.model.QItem;
import com.wishboard.server.notifications.application.dto.ItemNotificationDto;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.model.QNotifications;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationsRepositoryImpl implements NotificationsRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ItemNotificationDto> findAllByUserId(Long userId) {
		List<Tuple> results = queryFactory
			.select(item, notifications)
			.from(notifications)
			.join(item).on(notifications.itemId.eq(item.id))
			.where(notifications.userId.eq(userId))
			.orderBy(notifications.createdAt.desc())
			.fetch();
		return results.stream()
			.map(tuple -> {
				Item item = tuple.get(QItem.item);
				Notifications notifications = tuple.get(QNotifications.notifications);
				if (ObjectUtils.isEmpty(notifications)) {
					return new ItemNotificationDto();
				}
				return ItemNotificationDto.of(item, notifications);
			}).toList();
	}

	@Override
	public List<ItemNotificationDto> findUpcomingNotificationsByUserId(Long userId) {
		LocalDateTime nowPlus30Min = LocalDateTime.now().plusMinutes(30);

		List<Tuple> results = queryFactory
			.select(item, notifications)
			.from(notifications)
			.join(item).on(notifications.itemId.eq(item.id))
			.where(
				notifications.userId.eq(userId),
				notifications.itemNotificationDate.loe(nowPlus30Min)
			)
			.orderBy(notifications.itemNotificationDate.asc())
			.fetch();
		return results.stream()
			.map(tuple -> {
				Item item = tuple.get(QItem.item);
				Notifications notifications = tuple.get(QNotifications.notifications);
				if (ObjectUtils.isEmpty(notifications)) {
					return new ItemNotificationDto();
				}
				return ItemNotificationDto.of(item, notifications);
			}).toList();
	}

	@Override
	public void deleteAllByUserId(Long userId) {
		queryFactory
			.delete(notifications)
			.where(notifications.userId.eq(userId))
			.execute();
	}
}
