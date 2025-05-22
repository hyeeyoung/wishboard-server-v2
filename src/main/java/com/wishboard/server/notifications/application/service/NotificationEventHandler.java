package com.wishboard.server.notifications.application.service;

import com.wishboard.server.item.domain.event.ItemCreatedEvent;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationsRepository notificationsRepository;

    @EventListener
    @Transactional
    public void handleItemCreatedEvent(ItemCreatedEvent event) {
        log.info("Received ItemCreatedEvent for item ID: {}, user ID: {}", event.getItemId(), event.getUserId());

        // Assuming Notifications.newInstance and repository are updated to handle the new structure
        // from previous refactoring steps (using Long userId, Long itemId directly)
        Notifications notification = Notifications.newInstance(
                event.getUserId(),
                event.getItemId(),
                event.getItemNotificationType(),
                event.getItemNotificationDate()
                // If the Notifications entity is expanded in the future to store more details
                // like itemName, itemUrl, itemImageUrl, they can be set here from the event.
        );

        try {
            notificationsRepository.save(notification);
            log.info("Notification saved for item ID: {}, user ID: {}", event.getItemId(), event.getUserId());
        } catch (Exception e) {
            // Log the error and decide on error handling strategy
            // For example, re-throw to let the transaction roll back,
            // or handle specific exceptions differently.
            log.error("Error saving notification for item ID: {}, user ID: {}. Error: {}",
                    event.getItemId(), event.getUserId(), e.getMessage(), e);
            // Depending on the desired behavior, you might re-throw the exception
            // throw e;
        }
    }
}
