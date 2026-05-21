package com.eatproject.backend.member.service;

import com.eatproject.backend.member.dto.SecurityUserDto;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        if (Boolean.TRUE.equals(member.getIsBanned())) {
            throw new DisabledException("활동이 제한된 계정입니다.");
        }

        // 권한 설정 (ROLE_ 접두사 추가)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + member.getRole().name());

        // [수정 포인트] User가 아닌 SecurityUserDto를 생성하여 반환.
        return new SecurityUserDto(
                member.getEmail(),      // Username
                member.getPassword(),   // Password
                Collections.singletonList(authority), // Authorities
                member.getNickname()    // carNumber 자리에 일단 닉네임을 전달 (또는 "N/A")
        );
    }
}