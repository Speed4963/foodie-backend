package com.eatproject.backend.notification.event;



import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEvent {

    private String targetEmail;

    private Long postId;
    private Integer boardId;

    private String keyword;
}