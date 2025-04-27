package com.wishboard.server.domain.notifications.repository;

import static com.wishboard.server.domain.item.QItem.*;
import static com.wishboard.server.domain.notifications.QNotifications.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.item.QItem;
import com.wishboard.server.domain.notifications.Notifications;
import com.wishboard.server.domain.notifications.QNotifications;
import com.wishboard.server.service.notifications.dto.ItemNotificationDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationsRepositoryImpl implements NotificationsRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ItemNotificationDto> findAllByUserId(Long userId) {
		List<Tuple> results = queryFactory
			.select(item, notifications)
			.from(notifications)
			.join(item).on(notifications.notificationId.item.eq(item))
			.where(notifications.notificationId.user.id.eq(userId))
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
			.join(item).on(notifications.notificationId.item.eq(item))
			.where(
				notifications.notificationId.user.id.eq(userId),
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
			.where(notifications.notificationId.user.id.eq(userId))
			.execute();
	}
}
