package com.eatproject.backend.blog.controller;

import com.eatproject.backend.blog.dto.BlogPostRequestDto;
import com.eatproject.backend.blog.dto.BlogPostResponseDto;
import com.eatproject.backend.blog.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ──────────────────────────────────────────────────────────────
 *  프론트엔드 BlogPage.tsx > api 객체 매핑표
 * ──────────────────────────────────────────────────────────────
 *  api.getPosts(params)         →  GET    /api/posts?sort=&area=
 *  api.createPost(data)         →  POST   /api/posts
 *  api.updatePost(id, data)     →  PUT    /api/posts/{id}
 *  api.deletePost(id)           →  DELETE /api/posts/{id}
 *  api.toggleLike(id)           →  POST   /api/posts/{id}/like
 * ──────────────────────────────────────────────────────────────
 *
 *  기존 PostController (/api/community/posts) 는 건드리지 않음.
 *  이 컨트롤러는 /api/posts 경로 전용으로 블로그 기능만 담당.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Log4j2
public class BlogPostController {

    private final BlogPostService blogPostService;

    @Value("${image.upload-dir}")
    private String UPLOAD_DIR;

    // ── 헬퍼: SecurityContext에서 현재 사용자 ID 추출 ──────────────
    private static String callerId(UserDetails userDetails) {
        return userDetails != null ? userDetails.getUsername() : null;
    }

    /**
     * GET /api/posts?sort=latest&area=강남
     *
     * 인증 없이도 조회 가능 (SecurityConfig에서 permitAll 설정).
     * 인증된 사용자라면 liked 필드가 정확히 채워짐.
     */
    @GetMapping
    public ResponseEntity<List<BlogPostResponseDto>> getPosts(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String area,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                blogPostService.getPosts(sort, area, callerId(userDetails)));
    }

    /**
     * GET /api/posts/{id}  (단건 조회 — 필요 시 프론트에서 사용)
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostResponseDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                blogPostService.getPost(id, callerId(userDetails)));
    }

    /**
     * POST /api/posts
     * Authorization: Bearer <eatpick_access_token>
     *
     * 프론트 전송 body:
     * { restaurant, category, area, title, content, rating,
     *   photos: string[], tags: string[],
     *   authorId, author, authorColor }
     */
    @PostMapping
    public ResponseEntity<BlogPostResponseDto> createPost(
            @RequestBody @Valid BlogPostRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        BlogPostResponseDto created =
                blogPostService.createPost(requestDto, callerId(userDetails));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/posts/{id}
     * Authorization: Bearer <eatpick_access_token>
     */
    @PutMapping("/{id}")
    public ResponseEntity<BlogPostResponseDto> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid BlogPostRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                blogPostService.updatePost(id, requestDto, callerId(userDetails)));
    }

    /**
     * DELETE /api/posts/{id}
     * Authorization: Bearer <eatpick_access_token>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        blogPostService.deletePost(id, callerId(userDetails));
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/posts/{id}/like
     * Authorization: Bearer <eatpick_access_token>
     *
     * 이미 좋아요 → 취소 / 아니면 → 추가
     * 응답: 최신 BlogPost(liked, likes 갱신됨)
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<BlogPostResponseDto> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                blogPostService.toggleLike(id, callerId(userDetails)));
    }

    /**
     * POST /api/posts/images/upload
     * 블로그 게시글용 이미지 업로드
     * 응답: ["http://서버주소/api/posts/uploads/파일명", ...]
     */
    @PostMapping("/images/upload")
    public ResponseEntity<List<String>> uploadImages(
            @RequestParam("files") List<MultipartFile> files) {

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                String fileName = UUID.randomUUID().toString();
                File dest = new File(UPLOAD_DIR + fileName);
                file.transferTo(dest);

                fileUrls.add("/uploads/" + fileName);
            } catch (IOException e) {
                log.error("블로그 이미지 저장 실패", e);
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.ok(fileUrls);
    }

    /**
     * GET /api/posts/uploads/{uuid}
     * 블로그 이미지 조회 (inline — <img src> 태그에서 바로 표시)
     */
    @GetMapping("/uploads/{uuid}")
    public ResponseEntity<byte[]> viewImage(@PathVariable String uuid) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, uuid);

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] fileBytes = Files.readAllBytes(filePath);

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + uuid + "\"")
                    .body(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
