package com.eatproject.backend.notification.event;

import com.eatproject.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminEvent {

    private NotificationType type;

    private String targetEmail;

    private Integer boardId;
    private String keyword;
}