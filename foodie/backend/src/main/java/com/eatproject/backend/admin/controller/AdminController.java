package com.eatproject.backend.admin.controller;

import com.eatproject.backend.admin.dto.AdminLogResponseDto;
import com.eatproject.backend.admin.dto.SiteConfigDto;
import com.eatproject.backend.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users/{email}/ban")
    public ResponseEntity<Void> banUser(@PathVariable String email, @RequestParam String reason) {
        String adminEmail = "admin@eatproject.com";
        adminService.banUser(adminEmail, email, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/boards/{boardId}/approve")
    public ResponseEntity<Void> approveBoard(@PathVariable Integer boardId, @RequestParam String reason) {
        String adminEmail = "admin@eatproject.com";
        adminService.approveBoard(adminEmail, boardId, reason);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/config")
    public ResponseEntity<Void> updateConfig(@Valid @RequestBody SiteConfigDto configDto) {
        String adminEmail = "admin@eatproject.com";
        adminService.updateSiteConfig(adminEmail, configDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AdminLogResponseDto>> getLogs() {
        return ResponseEntity.ok(adminService.getAllLogs());
    }
}