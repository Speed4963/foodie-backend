package com.eatproject.backend.member.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 스프링 시큐리티가 인증 과정에서 사용할 유저 정보 클래스입니다.
 * 기본 User 클래스(email, password, authorities)에
 * 우리 서비스에 필요한 '차량 번호'를 추가했습니다.
 */
@Getter
public class SecurityUserDto extends User {

    private final String nickname; // ERD의 NICKNAME 필드 반영

    public SecurityUserDto(String email,
                           String password,
                           Collection<? extends GrantedAuthority> authorities,
                           String nickname) {
        // 부모 클래스인 User에게 email(username), password, authorities 전달
        super(email, password, authorities);
        this.nickname = nickname;
    }
}