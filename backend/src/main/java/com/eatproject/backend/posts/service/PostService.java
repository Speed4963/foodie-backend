    package com.eatproject.backend.posts.service;

    import com.eatproject.backend.admin.entity.SiteConfig;
    import com.eatproject.backend.admin.repository.SiteConfigRepository;
    import com.eatproject.backend.board.entity.Board;
    import com.eatproject.backend.board.repository.BoardRepository;
    import com.eatproject.backend.member.entity.Member;
    import com.eatproject.backend.member.repository.MemberRepository;
    import com.eatproject.backend.notification.entity.NotificationType;
    import com.eatproject.backend.notification.event.UserEvent;
    import com.eatproject.backend.posts.dto.PostRequestDto;
    import com.eatproject.backend.posts.dto.PostResponseDto;
    import com.eatproject.backend.posts.entity.Post;
    import com.eatproject.backend.posts.repository.PostRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.security.core.context.SecurityContextHolder;
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


        public PostResponseDto createPost(PostRequestDto requestDto) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Member writer = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("가입된 유저만 글을 쓸 수 있습니다."));
            Board board = boardRepository.findById(requestDto.getBoardId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

            if ("blog".equals(board.getSlug()) || "magazine".equals(board.getSlug())) {
                if (writer.getRole() != Member.Role.EDITOR && writer.getRole() != Member.Role.ADMIN) {
                    throw new IllegalStateException("이 공간은 에디터 권한이 있는 사용자만 칼럼을 작성할 수 있습니다.");
                }
            }

            SiteConfig config = siteConfigRepository.findById(1)
                    .orElseThrow(() -> new IllegalStateException("시스템 설정이 존재하지 않습니다."));

            if (requestDto.getParentId() != null) {

                Post parentThread = postRepository.findById(requestDto.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스레드입니다."));

                if (parentThread.getIsLocked()) {
                    throw new IllegalStateException("잠긴 스레드에는 답글을 작성할 수 없습니다.");
                }

                parentThread.updateOnNewReply();

                if (parentThread.getReplyCount() >= config.getThreadReplyLimit()) {
                    parentThread.lockThread();

                    eventPublisher.publishEvent(
                            new UserEvent(
                                    NotificationType.THREAD_LOCKED,
                                    "SYSTEM",
                                    parentThread.getWriter(),
                                    parentThread.getPostId(),
                                    parentThread.getBoard().getBoardId(),
                                    null
                            )
                    );
                }

                // 💡 수정됨: createEntity 호출 시 writer(Member 객체) 전달
                Post reply = createEntity(requestDto, writer, board, 1);
                Post savedReply = postRepository.save(reply);

                eventPublisher.publishEvent(
                        new UserEvent(
                                parentThread.getParentId() == null
                                        ? NotificationType.COMMENT_CREATED
                                        : NotificationType.REPLY_CREATED,
                                writer.getEmail(), // 💡 수정됨: requestDto.getWriter() 대신 writer.getEmail() 사용
                                parentThread.getWriter(),
                                parentThread.getPostId(),
                                parentThread.getBoard().getBoardId(),
                                null
                        )
                );

                return new PostResponseDto(savedReply);
            }

            if (board.getPostCount() >= config.getBoardThreadLimit()) {
                throw new IllegalStateException("게시판의 최대 스레드 개수에 도달했습니다.");
            }

            board.incrementPostCount();

            // 💡 수정됨: createEntity 호출 시 writer(Member 객체) 전달
            Post opPost = createEntity(requestDto, writer, board, 0);

            return new PostResponseDto(postRepository.save(opPost));
        }

        // 💡 핵심 수정됨: 파라미터로 Member writer를 받고, writer.getEmail()을 저장
        private Post createEntity(PostRequestDto dto, Member writer, Board board, int depth) {
            return Post.builder()
                    .board(board)
                    .parentId(dto.getParentId())
                    .quoteId(dto.getQuoteId())
                    .depth(depth)
                    .writer(writer.getEmail()) // DB 제약조건인 이메일을 정확히 저장
                    .content(dto.getContent())
                    .isAnonymous(dto.getIsAnonymous())
                    .imgUrl(dto.getImgUrl())
                    .thumbUrl(dto.getThumbUrl())
                    .build();
        }

        public Page<PostResponseDto> getBlogPosts(Pageable pageable) {

            Board blogBoard = boardRepository.findBySlug("blog")
                    .orElseThrow(() -> new IllegalArgumentException("blog board 없음"));

            return postRepository
                    .findAllByBoard_BoardIdAndDepthOrderByBumpAtDesc(
                            blogBoard.getBoardId(), 0, pageable)
                    .map(PostResponseDto::new);
        }

        public PostResponseDto getPostDetail(Long postId) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("POST 없음"));

            return new PostResponseDto(post);
        }

        @Transactional
        public void deletePost(Long postId) {
            // 1. 게시글 조회
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

            // 2. 권한 확인
            String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!post.getWriter().equals(currentEmail) && !isAdmin) {
                throw new IllegalStateException("본인의 글만 삭제할 수 있습니다.");
            }

            // 3. [핵심] 원문 삭제 시 답글까지 삭제하는 로직
            // parentId가 현재 postId인 모든 답글을 먼저 찾아 삭제합니다.
            if (post.getDepth() == 0) {
                List<Post> replies = postRepository.findAllByParentIdOrderByCreatedAtAsc(postId);
                if (!replies.isEmpty()) {
                    postRepository.deleteAll(replies);
                }
            }

            // 4. 본글 삭제
            postRepository.delete(post);
        }
//        좋아요
@Transactional
public PostResponseDto toggleLike(Long postId, boolean isIncrease) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

    post.updateLikeCount(isIncrease);
    return new PostResponseDto(postRepository.save(post));
}
    }