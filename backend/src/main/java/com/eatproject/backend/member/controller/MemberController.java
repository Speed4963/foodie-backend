package com.eatproject.backend.member.controller;

import com.eatproject.backend.member.dto.MemberDto;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping
    public ResponseEntity<Page<MemberDto>> getMemberList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getMemberList(pageable));
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
    @PatchMapping("/{email}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable String email,
            @RequestParam boolean isSuspend) { // true면 정지, false면 복구
        service.updateMemberStatus(email, isSuspend);
        return ResponseEntity.noContent().build();
    }
}