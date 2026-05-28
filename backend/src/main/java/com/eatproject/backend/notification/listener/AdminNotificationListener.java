package com.eatproject.backend.notification.listener;

import com.eatproject.backend.notification.entity.NotificationType;
import com.eatproject.backend.notification.event.AdminEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminNotificationListener {

    private final NotificationService notificationService;

    @EventListener
    public void handle(AdminEvent event) {

        notificationService.dispatch(
                NotificationType.BOARD_RECOMMEND,
                null,                       // actorEmail (없으면 null)
                event.getTargetEmail(),     // targetEmail
                null,                       // postId
                event.getBoardId(),         // boardId
                event.getKeyword()          // keyword
        );
    }
}