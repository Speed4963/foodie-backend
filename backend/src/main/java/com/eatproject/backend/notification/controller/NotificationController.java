package com.eatproject.backend.notification.controller;

import com.eatproject.backend.notification.dto.NotificationDto;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    // 내 알림 조회
    @GetMapping
    public List<NotificationDto> get(@RequestParam String email) {

        return service.getUserNotifications(email);
    }

    // 읽음 처리
    @PatchMapping("/{id}")
    public void read(@PathVariable Long id) {
        service.read(id);
    }
}