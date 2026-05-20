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


    @EventListener
    public void handle(UserEvent event) {

        notificationService.dispatch(
                NotificationType.COMMENT_CREATED,
                null,
                event.getTargetEmail(),
                event.getPostId(),
                event.getBoardId(),
                event.getKeyword()
        );
    }
}