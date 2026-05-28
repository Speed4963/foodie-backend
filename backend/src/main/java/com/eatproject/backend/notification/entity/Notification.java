package com.eatproject.backend.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiId;

    private String targetEmail;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Boolean isRead = false;

    private Long refPostId;

    private Integer refBoardId;

    private String keyword;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification(String targetEmail,
                        NotificationType type,
                        String message,
                        Long refPostId,
                        Integer refBoardId,
                        String keyword) {
        this.targetEmail = targetEmail;
        this.type = type;
        this.refPostId = refPostId;
        this.refBoardId = refBoardId;
        this.keyword = keyword;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}