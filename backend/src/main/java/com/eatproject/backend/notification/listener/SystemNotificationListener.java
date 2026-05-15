package com.eatproject.backend.notification.listener;

import lombok.RequiredArgsConstructor;
import notification.entity.NotificationType;
import notification.event.SystemEvent;
import notification.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemNotificationListener {

    private final NotificationService service;

    // 시스템 점검 등
    @EventListener
    public void handle(SystemEvent e) {

        service.create(
                e.getTargetEmail(),
                NotificationType.SYSTEM_MAINTENANCE,
                null,
                null,
                null
        );
    }
}