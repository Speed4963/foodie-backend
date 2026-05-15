package com.eatproject.backend.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.eatproject.backend.notification.entity.Notification;
import com.eatproject.backend.notification.entity.NotificationType;
import com.eatproject.backend.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;


    public void dispatch(NotificationType type,
                         String actorEmail,
                         String targetEmail,
                         Long postId,
                         Integer boardId,
                         String keyword) {

        switch (type) {

            // 글 작성자 대상
            case COMMENT_CREATED:
            case POST_LIKED:
            case THREAD_LOCKED:
            case POST_HIDDEN:
                sendToWriter(targetEmail, type, postId, boardId, keyword);
                break;

            //  특정 유저 대상 (멘션 / 차단)
            case MENTION:
            case USER_BANNED:
                sendToSingleUser(targetEmail, type, postId, boardId, keyword);
                break;

            // 작성자 + 참여자
            case THREAD_MIGRATED:
                sendToParticipants(targetEmail, type, postId, boardId, keyword);
                break;

            // 게시판 제안자
            case BOARD_ARCHIVED:
            case BOARD_STATUS_CHANGED:
                sendToBoardOwner(targetEmail, type, postId, boardId, keyword);
                break;

            //  관리자
            case BOARD_RECOMMEND:
            case SYSTEM_CONFIG_CHANGED:
                sendToAdmin(type, postId, boardId, keyword);
                break;

            // 전체 유저
            case SYSTEM_MAINTENANCE:
                broadcastToAll(type, postId, boardId, keyword);
                break;
        }
    }



    private void sendToWriter(String email,
                              NotificationType type,
                              Long postId,
                              Integer boardId,
                              String keyword) {
        save(email, type, postId, boardId, keyword);
    }

    private void sendToSingleUser(String email,
                                  NotificationType type,
                                  Long postId,
                                  Integer boardId,
                                  String keyword) {
        save(email, type, postId, boardId, keyword);
    }

    private void sendToParticipants(String email,
                                    NotificationType type,
                                    Long postId,
                                    Integer boardId,
                                    String keyword) {
        // TODO: 참여자 리스트 확장 가능
        save(email, type, postId, boardId, keyword);
    }

    private void sendToBoardOwner(String email,
                                  NotificationType type,
                                  Long postId,
                                  Integer boardId,
                                  String keyword) {
        save(email, type, postId, boardId, keyword);
    }

    private void sendToAdmin(NotificationType type,
                             Long postId,
                             Integer boardId,
                             String keyword) {
        // TODO: ADMIN 이메일 리스트 또는 ROLE 기반
        save("admin@system", type, postId, boardId, keyword);
    }

    private void broadcastToAll(NotificationType type,
                                Long postId,
                                Integer boardId,
                                String keyword) {
        // TODO: 전체 유저 조회 후 bulk insert 가능
        save("ALL_USERS", type, postId, boardId, keyword);
    }


    private void save(String targetEmail,
                      NotificationType type,
                      Long postId,
                      Integer boardId,
                      String keyword) {

        Notification n = new Notification(
                targetEmail,
                type,
                postId,
                boardId,
                keyword
        );

        repository.save(n);
    }


    public List<Notification> getUserNotifications(String email) {
        return repository.findByTargetEmailOrderByCreatedAtDesc(email);
    }

    @Transactional
    public void read(Long id) {
        Notification n = repository.findById(id).orElseThrow();
        n.markAsRead();
    }
}