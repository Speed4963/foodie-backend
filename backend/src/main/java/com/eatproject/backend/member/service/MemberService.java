package com.eatproject.backend.member.service;

import com.eatproject.backend.member.dto.MemberDto;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import com.eatproject.backend.common.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder managerBuilder;

    public String login(MemberDto memberDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberDto.getEmail(), memberDto.getPassword());

        Authentication auth = managerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return jwtUtils.generateJwtToken(auth);
    }

    @Transactional
    public void register(MemberDto memberDto) {
        if (repository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        if (repository.existsByNickname(memberDto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(encoder.encode(memberDto.getPassword()))
                .nickname(memberDto.getNickname())
                .role(Member.Role.USER) // Enum 상수 사용
                .isBanned(false)
                .build();

        repository.save(member);
    }

    public MemberDto findByEmail(String email) {
        Member member = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. email=" + email));

        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole()) // Enum 타입 그대로 전달
                .isBanned(member.getIsBanned())
                .createdAt(member.getCreatedAt() != null ?
                        member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "")
                .build();
    }
}