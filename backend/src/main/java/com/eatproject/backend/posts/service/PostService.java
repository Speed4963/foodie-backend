package com.eatproject.backend.posts.service;

import com.eatproject.backend.admin.entity.SiteConfig;
import com.eatproject.backend.admin.repository.SiteConfigRepository;
import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.board.repository.BoardRepository;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import com.eatproject.backend.notification.enums.EventType;
import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.posts.dto.PostRequestDto;
import com.eatproject.backend.posts.dto.PostResponseDto;
import com.eatproject.backend.posts.entity.Post;
import com.eatproject.backend.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final SiteConfigRepository siteConfigRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<PostResponseDto> getThreadsByBoard(Integer boardId, Pageable pageable) {
        return postRepository.findAllByBoard_BoardIdAndDepthOrderByBumpAtDesc(boardId, 0, pageable)
                .map(PostResponseDto::new);
    }

    public List<PostResponseDto> getRepliesByThread(Long threadId) {
        return postRepository.findAllByParentIdOrderByCreatedAtAsc(threadId).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        // 1. 유저 및 게시판 조회
        Member writer = memberRepository.findById(requestDto.getWriter())
                .orElseThrow(() -> new IllegalArgumentException("가입된 유저만 글을 쓸 수 있습니다."));

        Board board = boardRepository.findById(requestDto.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        // 2. 블루리본 매거진 스타일 권한 체크 (수정된 부분)
        // Board 엔티티에 추가한 isMagazineType() 메서드 혹은 slug 비교 사용
        if ("blog".equals(board.getSlug()) || "magazine".equals(board.getSlug())) {
            if (writer.getRole() != Member.Role.EDITOR && writer.getRole() != Member.Role.ADMIN) {
                throw new IllegalStateException("이 공간은 에디터 권한이 있는 사용자만 칼럼을 작성할 수 있습니다.");
            }
        }

        // 3. 사이트 설정값 조회
        SiteConfig config = siteConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalStateException("시스템 설정이 존재하지 않습니다."));

        // [답글(Reply) 로직]
        if (requestDto.getParentId() != null) {
            Post parentThread = postRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스레드입니다."));



            if (parentThread.getIsLocked()) {
                throw new IllegalStateException("잠긴 스레드에는 답글을 작성할 수 없습니다.");
            }

            // 원문(OP) 업데이트 (기존 엔티티의 메서드 호출)
            parentThread.updateOnNewReply();

            // [수정 포인트] 엔티티에 이미 정의된 lockThread() 호출
            if (parentThread.getReplyCount() >= config.getThreadReplyLimit()) {
                parentThread.lockThread();

                //  THREAD_LOCKED 이벤트-Esther 추가
                eventPublisher.publishEvent(
                        new ActionEvent(
                                EventType.THREAD_LOCKED,
                                "SYSTEM",
                                parentThread.getWriter(),
                                parentThread.getPostId(),
                                parentThread.getBoard().getBoardId(),
                                "스레드가 자동 잠금되었습니다."
                        )
                );
            }

            Post reply = createEntity(requestDto, board, 1);
            Post savedReply = postRepository.save(reply);

            // COMMENT / REPLY 이벤트 - Esther 추가
            eventPublisher.publishEvent(
                    new ActionEvent(
                            parentThread.getParentId() == null ? EventType.COMMENT : EventType.REPLY,
                            requestDto.getWriter(),                 // 작성자
                            parentThread.getWriter(),               // 알림 대상
                            parentThread.getPostId(),
                            parentThread.getBoard().getBoardId(),
                            "댓글이 달렸습니다."
                    )
            );

            return new PostResponseDto(savedReply);
        }

        // [스레드 원문(OP) 로직]
        if (board.getPostCount() >= config.getBoardThreadLimit()) {
            throw new IllegalStateException("게시판의 최대 스레드 개수에 도달했습니다.");
        }

        board.incrementPostCount();
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