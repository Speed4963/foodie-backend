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