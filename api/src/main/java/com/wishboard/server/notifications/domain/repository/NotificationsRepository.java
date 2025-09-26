package com.wishboard.server.notifications.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.notifications.domain.model.NotificationId;
import com.wishboard.server.notifications.domain.model.Notifications;

public interface NotificationsRepository extends JpaRepository<Notifications, Long>, NotificationsRepositoryCustom {
	Optional<Notifications> findByNotificationId(NotificationId id);
}
