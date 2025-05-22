package com.wishboard.server.notifications.application.service;

import com.wishboard.server.common.domain.ItemNotificationType;
import com.wishboard.server.item.domain.event.ItemCreatedEvent;
import com.wishboard.server.notifications.domain.model.Notifications;
import com.wishboard.server.notifications.domain.repository.NotificationsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when; // Not strictly needed if save returns void or the passed entity

@ExtendWith(MockitoExtension.class)
class NotificationEventHandlerTest {

    @InjectMocks
    private NotificationEventHandler notificationEventHandler;

    @Mock
    private NotificationsRepository notificationsRepository;

    @Test
    @DisplayName("Test Case 1: Successful Notification Creation from Event")
    void handleItemCreatedEvent_Success() {
        // Setup
        Long itemId = 1L;
        Long userId = 100L;
        String itemName = "Test Item";
        ItemNotificationType notificationType = ItemNotificationType.RESTOCK;
        LocalDateTime notificationDate = LocalDateTime.now().plusDays(1);
        String itemUrl = "http://example.com/item/1";
        String itemImageUrl = "http://example.com/image.jpg";

        ItemCreatedEvent event = new ItemCreatedEvent(
                itemId, userId, itemName, notificationType, notificationDate, itemUrl, itemImageUrl
        );

        // Mocking notificationsRepository.save to return the passed entity (or a new one)
        // This is optional if the method returns void or if we don't need to assert on the returned value from save.
        // For this test, we are more interested in what is *passed* to save.
        // when(notificationsRepository.save(any(Notifications.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Action
        notificationEventHandler.handleItemCreatedEvent(event);

        // Verification
        ArgumentCaptor<Notifications> notificationCaptor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository, times(1)).save(notificationCaptor.capture());

        Notifications capturedNotification = notificationCaptor.getValue();
        assertThat(capturedNotification).isNotNull();
        assertThat(capturedNotification.getUserId()).isEqualTo(userId);
        assertThat(capturedNotification.getItemId()).isEqualTo(itemId);
        assertThat(capturedNotification.getItemNotificationType()).isEqualTo(notificationType);
        assertThat(capturedNotification.getItemNotificationDate()).isEqualTo(notificationDate);
        assertThat(capturedNotification.getReadState()).isFalse(); // Assuming default is false
    }

    @Test
    @DisplayName("Test Case 2: Handle Event with Null Optional Fields")
    void handleItemCreatedEvent_WithNullOptionalFields() {
        // Setup
        Long itemId = 2L;
        Long userId = 200L;
        ItemNotificationType notificationType = ItemNotificationType.SALE_START;
        LocalDateTime notificationDate = LocalDateTime.now().plusHours(5);

        // Optional fields are null
        ItemCreatedEvent eventWithNulls = new ItemCreatedEvent(
                itemId, userId, null, notificationType, notificationDate, null, null
        );

        // Action
        notificationEventHandler.handleItemCreatedEvent(eventWithNulls);

        // Verification
        ArgumentCaptor<Notifications> notificationCaptor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository, times(1)).save(notificationCaptor.capture());

        Notifications capturedNotification = notificationCaptor.getValue();
        assertThat(capturedNotification).isNotNull();
        assertThat(capturedNotification.getUserId()).isEqualTo(userId);
        assertThat(capturedNotification.getItemId()).isEqualTo(itemId);
        assertThat(capturedNotification.getItemNotificationType()).isEqualTo(notificationType);
        assertThat(capturedNotification.getItemNotificationDate()).isEqualTo(notificationDate);
        assertThat(capturedNotification.getReadState()).isFalse();
        
        // Note: The current Notifications entity constructor used by newInstance
        // (Notifications.newInstance(userId, itemId, itemNotificationType, itemNotificationDate))
        // does not use itemName, itemUrl, or itemImageUrl. So, their nullness in the event
        // doesn't affect the core fields of the Notification entity. This test primarily ensures
        // that the handler logic itself doesn't break if these event fields are null.
    }
}
