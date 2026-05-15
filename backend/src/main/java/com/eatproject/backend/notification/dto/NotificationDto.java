package com.eatproject.backend.notification.dto;

package notification.dto;

import lombok.Getter;
import notification.entity.Notification;
import notification.entity.NotificationType;

@Getter
public class NotificationDto {

    private final Long id;
    private final NotificationType type;
    private final Boolean isRead;

    public NotificationDto(Notification n) {
        this.id = n.getNotiId();
        this.type = n.getType();
        this.isRead = n.getIsRead();
    }
}