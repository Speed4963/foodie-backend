package com.eatproject.backend.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminEvent {

    private String targetEmail;

    private Integer boardId;
    private String keyword;
}