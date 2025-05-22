package com.wishboard.server.item.domain.event;

import com.wishboard.server.common.domain.ItemNotificationType;
import java.time.LocalDateTime;

public class ItemCreatedEvent {
    private final Long itemId;
    private final Long userId;
    private final String itemName;
    private final ItemNotificationType itemNotificationType;
    private final LocalDateTime itemNotificationDate;
    private final String itemUrl;
    private final String itemImageUrl;

    public ItemCreatedEvent(Long itemId, Long userId, String itemName, ItemNotificationType itemNotificationType,
                            LocalDateTime itemNotificationDate, String itemUrl, String itemImageUrl) {
        this.itemId = itemId;
        this.userId = userId;
        this.itemName = itemName;
        this.itemNotificationType = itemNotificationType;
        this.itemNotificationDate = itemNotificationDate;
        this.itemUrl = itemUrl;
        this.itemImageUrl = itemImageUrl;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getItemName() {
        return itemName;
    }

    public ItemNotificationType getItemNotificationType() {
        return itemNotificationType;
    }

    public LocalDateTime getItemNotificationDate() {
        return itemNotificationDate;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    // Optional: toString() for logging
    @Override
    public String toString() {
        return "ItemCreatedEvent{" +
                "itemId=" + itemId +
                ", userId=" + userId +
                ", itemName='" + itemName + '\'' +
                ", itemNotificationType=" + itemNotificationType +
                ", itemNotificationDate=" + itemNotificationDate +
                ", itemUrl='" + itemUrl + '\'' +
                ", itemImageUrl='" + itemImageUrl + '\'' +
                '}';
    }
}
