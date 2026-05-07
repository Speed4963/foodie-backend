package com.eatproject.backend.posts.service;

import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.board.repository.BoardRepository;
import com.eatproject.backend.posts.dto.PostRequestDto;
import com.eatproject.backend.posts.dto.PostResponseDto;
import com.eatproject.backend.posts.entity.Post;
import com.eatproject.backend.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    // 1. 특정 게시판의 스레드(OP) 목록 조회 (Bump 순)
    public Page<PostResponseDto> getThreadsByBoard(Integer boardId, Pageable pageable) {
        return postRepository.findAllByBoard_BoardIdAndDepthOrderByBumpAtDesc(boardId, 0, pageable)
                .map(PostResponseDto::new);
    }

    // 2. 특정 스레드의 답글 목록 조회
    public List<PostResponseDto> getRepliesByThread(Long threadId) {
        return postRepository.findAllByParentIdOrderByCreatedAtAsc(threadId).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    // 3. 게시글 작성 (OP 및 답글 통합)
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        Board board = boardRepository.findById(requestDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        // 답글(Reply)인 경우
        if (requestDto.getParentId() != null) {
            Post parentThread = postRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스레드입니다."));

            if (parentThread.getIsLocked()) {
                throw new IllegalStateException("잠긴 스레드에는 답글을 작성할 수 없습니다.");
            }

            // 원문(OP)의 댓글 수 증가 및 Bump 시간 갱신
            parentThread.updateOnNewReply();

            // 추후 여기에 SITE_CONFIGS.THREAD_REPLY_LIMIT 초과 확인 및 스레드 자동 잠금 로직 추가 필요

            Post reply = createEntity(requestDto, board, 1);
            return new PostResponseDto(postRepository.save(reply));
        }

        // 스레드 원문(OP)인 경우
        board.incrementPostCount(); // 게시판 전체 글 수 증가
        Post opPost = createEntity(requestDto, board, 0);
        return new PostResponseDto(postRepository.save(opPost));
    }

    private Post createEntity(PostRequestDto dto, Board board, int depth) {
        return Post.builder()
                .board(board)
                .parentId(dto.getParentId())
                .quoteId(dto.getQuoteId())
                .depth(depth)
                .writer(dto.getWriter())
                .content(dto.getContent())
                .isAnonymous(dto.getIsAnonymous())
                .imgUrl(dto.getImgUrl())
                .thumbUrl(dto.getThumbUrl())
                .build();
    }
}