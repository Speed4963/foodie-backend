package com.eatproject.backend.notification.controller;

import com.eatproject.backend.notification.dto.NotificationDto;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    // 내 알림 조회
    @GetMapping
    public List<NotificationDto> get(Authentication auth) {
        return service.getUserNotifications(auth.getName());
    }

    // 읽음 처리
    @PatchMapping("/{id}")
    public void read(@PathVariable Long id) {
        service.read(id);
    }

    @GetMapping("/unread-count")
    public int count(Authentication auth) {
        return service.getUnreadCount(auth.getName());
    }
}