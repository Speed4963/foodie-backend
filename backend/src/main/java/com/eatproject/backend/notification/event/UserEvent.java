package com.eatproject.backend.notification.event;

import com.eatproject.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEvent {

    private NotificationType type;

    private String actorEmail;
    private String targetEmail;

    private Long postId;
    private Integer boardId;

    private String keyword;
}