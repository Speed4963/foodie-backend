package com.eatproject.backend.blog.repository;

import com.eatproject.backend.blog.entity.BlogPost;
import com.eatproject.backend.blog.entity.BlogPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogPostLikeRepository extends JpaRepository<BlogPostLike, Long> {
    Optional<BlogPostLike> findByPostAndLikerId(BlogPost post, String likerId);
    boolean existsByPostAndLikerId(BlogPost post, String likerId);
}
