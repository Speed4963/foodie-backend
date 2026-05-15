package com.eatproject.backend.notification.listener;

import lombok.RequiredArgsConstructor;
import notification.entity.NotificationType;
import notification.event.UserEvent;
import notification.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNotificationListener {

    private final NotificationService service;

    // 댓글, 좋아요, 멘션 등 사용자 이벤트
    @EventListener
    public void handle(UserEvent e) {

        service.create(
                e.getTargetEmail(),
                NotificationType.COMMENT_CREATED, // 연습용 고정
                e.getPostId(),
                e.getBoardId(),
                e.getKeyword()
        );
    }
}