package com.eatproject.backend.admin.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class AdminLogResponseDto {
    private Long logId;
    private String adminEmail;
    private String actionType;
    private String reason;
    private LocalDateTime createdAt;
}