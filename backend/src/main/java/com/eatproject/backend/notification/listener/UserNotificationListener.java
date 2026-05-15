package com.eatproject.backend.notification.listener;

import com.eatproject.backend.notification.entity.NotificationType;
import com.eatproject.backend.notification.event.UserEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserNotificationListener {

    private final NotificationService notificationService;

    /**
     * 👤 사용자 이벤트 알림 처리
     */
    @EventListener
    public void handle(UserEvent event) {

        notificationService.create(
                event.getTargetEmail(),
                NotificationType.COMMENT_CREATED, // 임시 고정
                event.getPostId(),
                event.getBoardId(),
                event.getKeyword()
        );
    }
}