package com.eatproject.backend.member.controller;

import com.eatproject.backend.member.dto.MemberDto;
import com.eatproject.backend.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService service;

    @PostMapping("/login")
    public ResponseEntity<MemberDto> login(@RequestBody MemberDto memberDto) {
        String jwt = service.login(memberDto);
        MemberDto loginUser = service.findByEmail(memberDto.getEmail());
        loginUser.setAccessToken(jwt);

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false) // HTTPS 환경이라면 true
                .path("/")
                .maxAge(60 * 60 * 24)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginUser);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody MemberDto memberDto) {
        service.register(memberDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(service.findByEmail(authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}