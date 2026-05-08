package com.eatproject.backend.posts.service;

import com.eatproject.backend.admin.entity.SiteConfig;
import com.eatproject.backend.admin.repository.SiteConfigRepository;
import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.board.repository.BoardRepository;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final SiteConfigRepository siteConfigRepository; // 설정값 조회를 위해 추가

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

        // 2. [수정 포인트] Blog 게시판 권한 가드
        // Member.Role이 Enum이 되었으므로, 타입에 맞춰 직접 비교합니다.
        if ("blog".equals(board.getSlug())) {
            // writer.getRole()이 이제 Member.Role 타입을 반환하므로 정상 작동합니다.
            if (writer.getRole() != Member.Role.EDITOR && writer.getRole() != Member.Role.ADMIN) {
                throw new IllegalStateException("블로그는 에디터 권한이 있는 유저만 작성 가능합니다.");
            }
        }

        // 3. 사이트 설정값 조회 (잠금 및 분기 기준 확인용)
        SiteConfig config = siteConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalStateException("시스템 설정이 존재하지 않습니다."));

        // [답글(Reply) 로직]
        if (requestDto.getParentId() != null) {
            Post parentThread = postRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스레드입니다."));

            if (parentThread.getIsLocked()) {
                throw new IllegalStateException("잠긴 스레드에는 답글을 작성할 수 없습니다.");
            }

            // 원문(OP) 업데이트 (Bump 및 Reply 카운트)
            parentThread.updateOnNewReply();

            // [추가] 스레드 자동 잠금 체크
            if (parentThread.getReplyCount() >= config.getThreadReplyLimit()) {
                parentThread.setIsLocked(true); // 엔티티에 Setter 혹은 lock() 메서드 필요
                // TODO: 필요 시 자동 다음 스레드 생성 로직 연동
            }

            Post reply = createEntity(requestDto, board, 1);
            return new PostResponseDto(postRepository.save(reply));
        }

        // [스레드 원문(OP) 로직]
        // [추가] 게시판 자동 분기 체크
        if (board.getPostCount() >= config.getBoardThreadLimit()) {
            // 이 시점에는 게시판을 아카이브하고 새 세대를 만들어야 합니다.
            // board.setStatus("ARCHIVED");
            // 현재는 간단히 에러를 내거나 관리를 유도할 수 있습니다.
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