package com.eatproject.backend.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String email; // 사용자 1명당 1개 토큰 (최소 구조)

    @Column(nullable = false, length = 512)
    private String token;

    private LocalDateTime expiryDate;


    public void updateToken(String newToken) {
        this.token = newToken;
    }
}