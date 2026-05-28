package com.eatproject.backend.blog.repository;

import com.eatproject.backend.blog.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    // ─── 정렬 전용 (지역 필터 없음) ──────────────────────────────

    @Query("SELECT p FROM BlogPost p ORDER BY p.createdAt DESC")
    List<BlogPost> findAllOrderByDateDesc();

    @Query("SELECT p FROM BlogPost p ORDER BY p.likes DESC")
    List<BlogPost> findAllOrderByLikesDesc();

    @Query("SELECT p FROM BlogPost p ORDER BY p.rating DESC")
    List<BlogPost> findAllOrderByRatingDesc();

    // ─── 지역 필터 + 정렬 ────────────────────────────────────────

    @Query("SELECT p FROM BlogPost p WHERE p.area = :area ORDER BY p.createdAt DESC")
    List<BlogPost> findByAreaOrderByDateDesc(@Param("area") String area);

    @Query("SELECT p FROM BlogPost p WHERE p.area = :area ORDER BY p.likes DESC")
    List<BlogPost> findByAreaOrderByLikesDesc(@Param("area") String area);

    @Query("SELECT p FROM BlogPost p WHERE p.area = :area ORDER BY p.rating DESC")
    List<BlogPost> findByAreaOrderByRatingDesc(@Param("area") String area);
}
