package com.eatproject.backend.member.entity;

import com.eatproject.backend.common.BaseTimeEntity; // 1. 상속을 위한 임포트
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where; // 2. 소프트 딜리트 필터링

@Entity
@Table(name = "MEMBERS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 3. 무분별한 객체 생성 방지
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
@EqualsAndHashCode(callSuper = false, of = "email") // 4. 상속 관계 고려
@Where(clause = "DELETED_AT IS NULL") // 5. 삭제된 회원은 기본 조회에서 제외
public class Member extends BaseTimeEntity { // 6. BaseTimeEntity 상속

    @Id
    @Column(name = "EMAIL", length = 255)
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 512)
    private String password;

    @Column(name = "NICKNAME", nullable = false, length = 50)
    private String nickname;

    // --- Role Enum ---
    public enum Role {
        USER, EDITOR, ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "IS_BANNED", nullable = false)
    @Builder.Default
    private Boolean isBanned = false;

    // [삭제] CREATED_AT, DELETED_AT 필드
    // 이유: BaseTimeEntity 상속을 통해 자동으로 관리됨
}