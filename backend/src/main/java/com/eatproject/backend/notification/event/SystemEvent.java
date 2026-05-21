package com.eatproject.backend.notification.event;

import com.eatproject.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemEvent {

    private NotificationType type;

    private String targetEmail;
}