package com.eatproject.backend.member.repository;

import com.eatproject.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    /**
     * 1) 이메일로 회원 정보 조회
     * PK가 이메일이므로 기본 제공되는 findById(String email)와 동일하게 동작하지만,
     * 명시적인 네이밍을 위해 추가할 수 있습니다.
     */
    Optional<Member> findByEmail(String email);

    /**
     * 2) 이메일 중복 체크
     * 회원가입 시 이미 존재하는 계정인지 확인할 때 사용합니다.
     */
    boolean existsByEmail(String email);

    /**
     * 3) 닉네임 중복 체크
     * 우리 앱은 NICKNAME을 중요하게 사용하므로, 가입 시 닉네임 중복 여부도 확인해야 합니다.
     */
    boolean existsByNickname(String nickname);

    /**
     * 4) 탈퇴하지 않은 회원 조회 (Soft Delete 대응)
     * DELETED_AT이 null인 유저만 찾습니다.
     */
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);
}