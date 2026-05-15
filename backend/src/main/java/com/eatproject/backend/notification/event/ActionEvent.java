package com.eatproject.backend.notification.event;

import com.eatproject.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionEvent {

    private NotificationType type;

    private String actor;      // 행동한 사람 (writer)
    private String target;     // 알림 받는 사람

    private Long postId;
    private Integer boardId;

    private String message;
}
