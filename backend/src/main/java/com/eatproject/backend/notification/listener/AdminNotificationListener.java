package com.eatproject.backend.notification.listener;

import lombok.RequiredArgsConstructor;
import notification.entity.NotificationType;
import notification.event.AdminEvent;
import notification.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminNotificationListener {

    private final NotificationService service;

    // 게시판 승인, 추천 등
    @EventListener
    public void handle(AdminEvent e) {

        service.create(
                e.getTargetEmail(),
                NotificationType.BOARD_RECOMMEND,
                null,
                e.getBoardId(),
                e.getKeyword()
        );
    }
}