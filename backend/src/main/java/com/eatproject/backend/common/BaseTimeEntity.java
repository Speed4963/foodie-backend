package com.eatproject.backend.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // ERD의 대부분 테이블(MEMBERS, BOARDS, POSTS, RESTAURANTS 등)이
    // 공통으로 사용하는 소프트 딜리트 필드
    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    /**
     * 소프트 딜리트 실행 메서드
     * 서비스 계층에서 entity.delete() 호출 시 사용
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부 확인 편의 메서드
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}