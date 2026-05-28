package com.eatproject.backend.notification.repository;

import com.eatproject.backend.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetEmailOrderByCreatedAtDesc(String targetEmail);
    int countByTargetEmailAndIsReadFalse(String email);
}