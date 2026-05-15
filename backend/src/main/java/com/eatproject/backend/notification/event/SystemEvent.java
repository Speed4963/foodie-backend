package com.eatproject.backend.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemEvent {

    private String targetEmail;
}