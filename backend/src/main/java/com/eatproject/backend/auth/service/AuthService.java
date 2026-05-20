package com.eatproject.backend.auth.service;

import com.eatproject.backend.auth.dto.TokenResponse;
import com.eatproject.backend.auth.entity.RefreshToken;
import com.eatproject.backend.auth.jwt.JwtProvider;
import com.eatproject.backend.auth.repository.RefreshTokenRepository;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    public TokenResponse login(String email, String password) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Member member = (Member) auth.getPrincipal();

        String role = member.getRole().name();

        String accessToken = jwtProvider.generateAccessToken(email, role);
        String refreshToken = jwtProvider.generateRefreshToken(email);

        RefreshToken tokenEntity = RefreshToken.builder()
                .email(email)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }
    public TokenResponse refresh(String refreshToken) {

        if (!jwtProvider.validate(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtProvider.getEmail(refreshToken);

        RefreshToken saved = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not found refresh token"));

        if (!saved.getToken().equals(refreshToken)) {
            throw new RuntimeException("Token mismatch");
        }

        Member member = memberRepository.findById(email)
                .orElseThrow();

        String role = member.getRole().name();

        String newAccessToken = jwtProvider.generateAccessToken(email, role);

        String newRefreshToken = jwtProvider.generateRefreshToken(email);

        saved.updateToken(newRefreshToken);
        refreshTokenRepository.save(saved);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

}