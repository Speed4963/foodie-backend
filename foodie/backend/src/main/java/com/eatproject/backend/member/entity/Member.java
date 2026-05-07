package com.eatproject.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEMBERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(name = "EMAIL", length = 255)
    private String email;

    @Column(name = "PASSWORD", length = 512, nullable = false)
    private String password;

    @Column(name = "NICKNAME", length = 50, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", length = 20, nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "IS_BANNED", nullable = false)
    @Builder.Default
    private Boolean isBanned = false;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    // Admin 서비스에서 호출할 상태 변경 메서드
    public void setIsBanned(Boolean banned) {
        this.isBanned = banned;
    }

    public enum Role {
        USER, EDITOR, ADMIN
    }
}