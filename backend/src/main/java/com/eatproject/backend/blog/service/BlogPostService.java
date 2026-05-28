package com.eatproject.backend.blog.service;

import com.eatproject.backend.blog.dto.BlogPostRequestDto;
import com.eatproject.backend.blog.dto.BlogPostResponseDto;
import com.eatproject.backend.blog.entity.BlogPost;
import com.eatproject.backend.blog.entity.BlogPostLike;
import com.eatproject.backend.blog.repository.BlogPostLikeRepository;
import com.eatproject.backend.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final BlogPostLikeRepository likeRepository;

    // ─── 1. 목록 조회 ────────────────────────────────────────────
    /**
     * GET /api/posts?sort=latest&area=강남
     * - sort  : latest(기본) | likes | rating
     * - area  : 없으면 전체 조회
     * - callerId : JWT에서 추출한 사용자 식별자 (비인증 시 null)
     *              → liked 필드를 올바르게 채우기 위해 사용
     */
    public List<BlogPostResponseDto> getPosts(String sort, String area, String callerId) {
        List<BlogPost> posts = fetchSorted(sort, area);
        return posts.stream()
                .map(p -> new BlogPostResponseDto(p, isLikedBy(p, callerId)))
                .collect(Collectors.toList());
    }

    // ─── 2. 단건 조회 ────────────────────────────────────────────
    public BlogPostResponseDto getPost(Long id, String callerId) {
        BlogPost post = findOrThrow(id);
        return new BlogPostResponseDto(post, isLikedBy(post, callerId));
    }

    // ─── 3. 게시글 생성 ──────────────────────────────────────────
    /**
     * POST /api/posts
     * callerId : JwtAuthenticationFilter 가 SecurityContext 에 주입한 subject(email)
     */
    @Transactional
    public BlogPostResponseDto createPost(BlogPostRequestDto dto, String callerId) {
        String authorId    = (callerId != null) ? callerId : dto.getAuthorId();
        String authorName  = dto.getAuthor()      != null ? dto.getAuthor()      : authorId;
        String authorColor = dto.getAuthorColor() != null ? dto.getAuthorColor() : "#E8272A";

        BlogPost post = BlogPost.builder()
                .authorId(authorId)
                .authorName(authorName)
                .authorColor(authorColor)
                .restaurant(dto.getRestaurant())
                .category(dto.getCategory())
                .area(dto.getArea())
                .title(dto.getTitle())
                .content(dto.getContent())
                .rating(dto.getRating())
                .build();

        post.setPhotos(dto.getPhotos());
        post.setTags(dto.getTags());

        return new BlogPostResponseDto(blogPostRepository.save(post), false);
    }

    // ─── 4. 게시글 수정 ──────────────────────────────────────────
    /**
     * PUT /api/posts/{id}
     * 본인 또는 ADMIN 만 수정 가능 (Controller 레이어에서 role 체크 후 호출)
     */
    @Transactional
    public BlogPostResponseDto updatePost(Long id, BlogPostRequestDto dto, String callerId) {
        BlogPost post = findOrThrow(id);
        checkOwnership(post, callerId);

        post.update(
                dto.getRestaurant(), dto.getCategory(), dto.getArea(),
                dto.getTitle(), dto.getContent(), dto.getRating(),
                dto.getPhotos(), dto.getTags()
        );

        return new BlogPostResponseDto(post, isLikedBy(post, callerId));
    }

    // ─── 5. 게시글 삭제 ──────────────────────────────────────────
    /**
     * DELETE /api/posts/{id}
     */
    @Transactional
    public void deletePost(Long id, String callerId) {
        BlogPost post = findOrThrow(id);
        checkOwnership(post, callerId);
        blogPostRepository.delete(post);
    }

    // ─── 6. 좋아요 토글 ──────────────────────────────────────────
    /**
     * POST /api/posts/{id}/like
     * 이미 눌렀으면 취소, 아니면 추가 → 최신 상태 반환
     */
    @Transactional
    public BlogPostResponseDto toggleLike(Long id, String callerId) {
        BlogPost post = findOrThrow(id);

        Optional<BlogPostLike> existing = likeRepository.findByPostAndLikerId(post, callerId);
        boolean nowLiked;

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            post.decrementLikes();
            nowLiked = false;
        } else {
            likeRepository.save(BlogPostLike.builder().post(post).likerId(callerId).build());
            post.incrementLikes();
            nowLiked = true;
        }

        return new BlogPostResponseDto(post, nowLiked);
    }

    // ─── Private 헬퍼 ────────────────────────────────────────────

    private BlogPost findOrThrow(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. id=" + id));
    }

    private boolean isLikedBy(BlogPost post, String callerId) {
        if (callerId == null) return false;
        return likeRepository.existsByPostAndLikerId(post, callerId);
    }

    /**
     * 작성자 본인 여부 확인
     * ADMIN 권한 체크는 Controller 레이어(@PreAuthorize)에서 처리하거나
     * callerId에 "ADMIN" 접두사 규칙을 사용하는 등 프로젝트 정책에 따라 조정
     */
    private void checkOwnership(BlogPost post, String callerId) {
        if (callerId == null || !callerId.equals(post.getAuthorId())) {
            throw new IllegalStateException("본인의 게시글만 수정/삭제할 수 있습니다.");
        }
    }

    private List<BlogPost> fetchSorted(String sort, String area) {
        boolean hasArea = area != null && !area.isBlank();
        return switch (sort == null ? "latest" : sort) {
            case "likes"  -> hasArea
                    ? blogPostRepository.findByAreaOrderByLikesDesc(area)
                    : blogPostRepository.findAllOrderByLikesDesc();
            case "rating" -> hasArea
                    ? blogPostRepository.findByAreaOrderByRatingDesc(area)
                    : blogPostRepository.findAllOrderByRatingDesc();
            default       -> hasArea
                    ? blogPostRepository.findByAreaOrderByDateDesc(area)
                    : blogPostRepository.findAllOrderByDateDesc();
        };
    }
}
