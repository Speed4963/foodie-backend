package com.eatproject.backend.notification.dto;

import com.eatproject.backend.notification.entity.Notification;
import lombok.Getter;

@Getter
public class NotificationDto {

    private final Long id;
    private final String type;
    private final String message;
    private final Boolean isRead;

    public NotificationDto(Notification n) {
        this.id = n.getNotiId();
        this.type = n.getType();
        this.message = n.getMessage();
        this.isRead = n.getIsRead();
    }
}