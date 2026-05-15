package com.eatproject.backend.notification.listener;

import com.eatproject.backend.notification.entity.NotificationType;
import com.eatproject.backend.notification.event.SystemEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemNotificationListener {

    private final NotificationService notificationService;


    @EventListener
    public void handle(SystemEvent event) {

        notificationService.create(
                event.getTargetEmail(),
                NotificationType.SYSTEM_MAINTENANCE,
                null,
                null,
                null
        );
    }
}