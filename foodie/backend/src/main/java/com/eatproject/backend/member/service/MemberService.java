package com.eatproject.backend.member.service;

import com.eatproject.backend.member.dto.LoginRequestDto;
import com.eatproject.backend.member.dto.SignupRequestDto;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequestDto dto) {
        if (memberRepository.existsById(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // 암호화 핵심
                .nickname(dto.getNickname())
                .build();

        memberRepository.save(member);
    }

    public Member login(LoginRequestDto dto) {
        Member member = memberRepository.findByEmailAndDeletedAtIsNull(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (member.getIsBanned()) {
            throw new IllegalStateException("차단된 유저입니다. 관리자에게 문의하세요.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}