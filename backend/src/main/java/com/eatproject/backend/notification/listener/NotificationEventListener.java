package com.eatproject.backend.notification.listener;

import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handle(ActionEvent event) {

        switch (event.getType()) {

            case COMMENT:
            case REPLY:
                notificationService.create(
                        event.getTargetEmail(),
                        event.getType().name(),
                        event.getPostId(),
                        event.getBoardId(),
                        "댓글이 달렸습니다."
                );
                break;

            case LIKE:
                notificationService.create(
                        event.getTargetEmail(),
                        "LIKE",
                        event.getPostId(),
                        null,
                        "좋아요가 증가했습니다."
                );
                break;

            case THREAD_LOCKED:
                notificationService.create(
                        event.getTargetEmail(),
                        "THREAD_LOCKED",
                        event.getPostId(),
                        null,
                        "스레드가 자동 잠금되었습니다."
                );
                break;

            case ACCOUNT_BANNED:
                notificationService.create(
                        event.getTargetEmail(),
                        "ACCOUNT_BANNED",
                        null,
                        null,
                        "계정이 정지되었습니다."
                );
                break;

            case BOARD_APPROVED:
                notificationService.create(
                        event.getTargetEmail(),
                        "BOARD_APPROVED",
                        null,
                        event.getBoardId(),
                        "게시판이 승인되었습니다."
                );
                break;
        }
    }
}