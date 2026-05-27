package com.eatproject.backend.notification.dto;

import com.eatproject.backend.notification.entity.Notification;
import com.eatproject.backend.notification.entity.NotificationType;
import lombok.Getter;

@Getter
public class NotificationDto {

    private final Long id;
    private final String content;
    private final Boolean isRead;

    public NotificationDto(Notification n) {
        this.id = n.getNotiId();     // ⭐ 여기 중요
        this.content = n.getType().getMessage();
        this.isRead = n.getIsRead(); // ⭐ Boolean getter
    }
}