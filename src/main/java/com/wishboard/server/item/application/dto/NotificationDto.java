package com.wishboard.server.item.application.dto;

import java.time.LocalDateTime;

import com.wishboard.server.common.domain.ItemNotificationType;
// import com.wishboard.server.notifications.domain.model.NotificationId; // Removed

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // Consider if setters are needed, or make DTO immutable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long notificationId; // This will be the new primary key of Notifications entity
    private Long userId;
    private Long itemId;
    private ItemNotificationType itemNotificationType;
    private LocalDateTime itemNotificationDate;
    private Boolean readState;
    // Consider adding other fields from Notifications entity if needed, e.g., createdAt, updatedAt
}
