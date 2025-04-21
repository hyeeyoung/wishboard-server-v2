package com.wishboard.server.domain.notifications.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.domain.notifications.NotificationId;
import com.wishboard.server.domain.notifications.Notifications;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
	Optional<Notifications> findByNotificationId(NotificationId id);
}
