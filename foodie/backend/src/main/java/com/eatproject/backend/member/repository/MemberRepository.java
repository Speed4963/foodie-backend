package com.eatproject.backend.member.repository;

import com.eatproject.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);
}