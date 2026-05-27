package com.eatproject.backend.posts.controller;

import com.eatproject.backend.posts.dto.PostRequestDto;
import com.eatproject.backend.posts.dto.PostResponseDto;
import com.eatproject.backend.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// 프론트엔드에서 요청하는 기본 URL 경로와 일치시킵니다.
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 1. 스레드 원문 및 답글 등록 (POST /api/community/posts)
     * 프론트엔드의 handleAddPost() 에서 호출되는 핵심 엔드포인트입니다.
     */
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = postService.createPost(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 2. 특정 게시판의 원문(스레드) 목록 조회 (GET /api/community/posts/board/{boardId})
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Page<PostResponseDto>> getThreadsByBoard(
            @PathVariable Integer boardId,
            @PageableDefault(size = 10, sort = "bumpAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDto> threads = postService.getThreadsByBoard(boardId, pageable);
        return ResponseEntity.ok(threads);
    }

    /**
     * 3. 특정 스레드의 답글 목록 조회 (GET /api/community/posts/{threadId}/replies)
     */
    @GetMapping("/{threadId}/replies")
    public ResponseEntity<List<PostResponseDto>> getRepliesByThread(@PathVariable Long threadId) {
        List<PostResponseDto> replies = postService.getRepliesByThread(threadId);
        return ResponseEntity.ok(replies);
    }
}