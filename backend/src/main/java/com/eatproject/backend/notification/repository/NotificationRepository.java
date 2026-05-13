package com.eatproject.backend.notification.repository;

import com.eatproject.backend.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetEmailOrderByCreatedAtDesc(String email);

    int countByTargetEmailAndTypeAndRefPostId(
            String email,
            String type,
            Long postId
    );
}