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

    /**
     * 🛠 관리자 이벤트 알림 처리
     * - 게시판 추천 / 승인 / 관리성 이벤트
     */
    @EventListener
    public void handle(AdminEvent event) {

        notificationService.create(
                event.getTargetEmail(),   // 수신자
                NotificationType.BOARD_RECOMMEND, // 알림 타입
                null,                      // message (필요 시 event에 추가)
                event.getBoardId(),       // 관련 게시글/게시판 ID
                event.getKeyword()        // 추가 정보
        );
    }
}