package com.eatproject.backend.member.service;

import com.eatproject.backend.member.dto.MemberDto;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import com.eatproject.backend.common.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    // AuthenticationManagerBuilder 대신 직접 AuthenticationManager를 사용합니다.
    // (SecurityConfig에서 @Bean으로 등록되어 있어야 합니다.)
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    /**
     * 로그인 로직
     * @param memberDto 이메일과 비밀번호가 담긴 DTO
     * @return 생성된 JWT 토큰
     */
    public String login(MemberDto memberDto) {
        // 1. 인증 토큰(Ticket) 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberDto.getEmail(), memberDto.getPassword());

        // 2. 실제 인증 시도
        // 이때 UserDetailsServiceImpl.loadUserByUsername()이 호출되어 DB 정보를 대조합니다.
        Authentication auth = authenticationManager.authenticate(authenticationToken);

        // 3. 인증 성공 시 시큐리티 컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 4. JwtUtils를 사용하여 SecurityUserDto 기반의 토큰 생성 및 반환
        return jwtUtils.generateJwtToken(auth);
    }

    /**
     * 회원가입 로직
     */
    @Transactional
    public void register(MemberDto memberDto) {
        // 이메일 중복 체크
        if (repository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        // 닉네임 중복 체크
        if (repository.existsByNickname(memberDto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // 패스워드 암호화 및 유저 저장
        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(encoder.encode(memberDto.getPassword()))
                .nickname(memberDto.getNickname())
                .role(Member.Role.USER) // 기본 권한 USER 부여
                .isBanned(false)
                .build();

        repository.save(member);
    }

    /**
     * 이메일로 회원 정보 조회
     */
    @Transactional(readOnly = true) // 성능 최적화를 위한 읽기 전용 설정
    public MemberDto findByEmail(String email) {
        Member member = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. email=" + email));

        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .isBanned(member.getIsBanned())
                .createdAt(member.getCreatedAt() != null ?
                        member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "")
                .build();
    }
    public Page<MemberDto> getMemberList(Pageable pageable) {
        return repository.findAll(pageable).map(member -> MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .isBanned(member.getIsBanned())
                .createdAt(member.getCreatedAt() != null ?
                        member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "")
                .build());
    }
    }
