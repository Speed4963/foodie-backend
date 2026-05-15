package com.eatproject.backend.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.eatproject.backend.notification.entity.Notification;
import com.eatproject.backend.notification.entity.NotificationType;
import com.eatproject.backend.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public void create(String targetEmail,
                       NotificationType type,
                       Long postId,
                       Integer boardId,
                       String keyword) {

        Notification n = new Notification(
                targetEmail,
                type,
                postId,
                boardId,
                keyword
        );

        repository.save(n);
    }

    public List<Notification> getUserNotifications(String email) {
        return repository.findByTargetEmailOrderByCreatedAtDesc(email);
    }

    @Transactional
    public void read(Long id) {
        Notification n = repository.findById(id).orElseThrow();
        n.markAsRead();
    }
}