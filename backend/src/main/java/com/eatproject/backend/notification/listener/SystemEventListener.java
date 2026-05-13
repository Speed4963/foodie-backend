package com.eatproject.backend.notification.listener;

import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemEventListener {

    private final NotificationService service;

    @EventListener(condition =
            "#event.type == T(com.eatproject.backend.notification.enums.EventType).SYSTEM_NOTICE || " +
                    "#event.type == T(com.eatproject.backend.notification.enums.EventType).TRENDING")
    public void handle(ActionEvent event) {

        service.create(
                event.getTargetEmail(),
                event.getType().name(),
                null,
                event.getBoardId(),
                event.getMessage()
        );
    }
}