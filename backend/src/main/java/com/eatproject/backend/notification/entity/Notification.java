package com.eatproject.backend.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiId;

    private String targetEmail;

    private String type;

    private Boolean isRead;

    private Integer refBoardId;

    private Long refPostId;

    private String message;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}