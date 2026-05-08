package com.eatproject.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEMBERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
@EqualsAndHashCode(of = "email")
public class Member {

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 512)
    private String password;

    @Column(name = "NICKNAME", nullable = false, length = 50)
    private String nickname;

    // --- Role Enum 추가 ---
    public enum Role {
        USER, EDITOR, ADMIN
    }

    @Enumerated(EnumType.STRING) // DB에 "USER", "ADMIN" 등 문자열로 저장
    @Column(name = "ROLE", nullable = false, length = 20)
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
}