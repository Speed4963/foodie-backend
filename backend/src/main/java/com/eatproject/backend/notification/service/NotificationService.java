package com.eatproject.backend.notification.service;

import com.eatproject.backend.notification.entity.Notification;
import com.eatproject.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public void create(String email, String type, Long postId, Integer boardId, String message) {

        repository.save(Notification.builder()
                .targetEmail(email)
                .type(type)
                .refPostId(postId)
                .refBoardId(boardId)
                .message(message)
                .isRead(false)
                .build());
    }
}