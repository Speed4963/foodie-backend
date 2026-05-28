package com.eatproject.backend.common.jwt;

import com.eatproject.backend.member.dto.SecurityUserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtils {

    @Value("${simpleDms.app.jwtSecret}")
    private String jwtSecret;

    @Value("${simpleDms.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // 안전한 SecretKey 생성을 위한 헬퍼 메서드
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1) 웹토큰(JWT) 생성 (로그인 성공 시 호출)
    public String generateJwtToken(Authentication authentication) {
        SecurityUserDto securityUserDto = (SecurityUserDto) authentication.getPrincipal();

        // 유저의 권한들을 리스트로 추출 (예: ["ROLE_USER"])
        List<String> roles = securityUserDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(securityUserDto.getUsername())         // 이메일
                .claim("nickname", securityUserDto.getNickname()) // 닉네임
                .claim("roles", roles)                            // 권한 리스트
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // HS512 암호화
                .compact();
    }

    // 2) 웹토큰에서 이메일 추출
    public String getUserNameFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // [유틸리티] 웹토큰에서 닉네임 추출
    public String getNicknameFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("nickname", String.class);
    }

    // 3) 웹토큰 유효성 검사
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 형식: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT 만료: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임이 비어 있음: {}", e.getMessage());
        }
        return false;
    }

    public Optional<String> getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    // 5) 헤더에서 JWT 추출 (Bearer 방식)
    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}