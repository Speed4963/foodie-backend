package com.eatproject.backend.auth.controller;

import com.eatproject.backend.auth.dto.TokenResponse;
import com.eatproject.backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login(@RequestParam String email,
                               @RequestParam String password) {
        return authService.login(email, password);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
