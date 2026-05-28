package com.eatproject.backend.notification.dto;

import com.eatproject.backend.notification.entity.Notification;
import com.eatproject.backend.notification.entity.NotificationType;
import lombok.Getter;

@Getter
public class NotificationDto {

    private final Long id;
    private final NotificationType type; // ENUM 상수명 (예: "COMMENT_CREATED")
    private final String message;        // ⭐ TODO 한글 메시지 (예: "댓글이 달렸습니다")
    private final Boolean isRead;

    public NotificationDto(Notification n) {
        this.id = n.getNotiId();
        this.type = n.getType();
        // ⭐ ENUM 내부에 정의된 getMessage()를 호출하여 값을 할당합니다.
        this.message = n.getType().getMessage();    // TODO 한글 메시지 (예: "댓글이 달렸습니다")
        this.isRead = n.getIsRead();
    }
}