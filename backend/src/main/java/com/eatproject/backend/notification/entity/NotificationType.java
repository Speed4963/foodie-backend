package com.eatproject.backend.notification.entity;



//public enum NotificationType {
//
//    COMMENT_CREATED,
//    POST_LIKED,
//    MENTION,
//
//    COMMENT,
//    REPLY,
//
//    THREAD_LOCKED,
//    THREAD_MIGRATED,
//
//    BOARD_ARCHIVED,
//    BOARD_STATUS_CHANGED,
//
//    BOARD_RECOMMEND,
//    SYSTEM_MAINTENANCE,
//
//    POST_HIDDEN,
//    USER_BANNED,
//
//
//
//    SYSTEM_CONFIG_CHANGED
//
//
//}


import lombok.Getter;

@Getter
public enum NotificationType {

    COMMENT_CREATED("댓글이 달렸습니다"),
    REPLY_CREATED("답글이 달렸습니다"),
    POST_LIKED("게시글에 좋아요가 눌렸습니다"),
    THREAD_LOCKED("스레드가 자동 잠금되었습니다"),
    THREAD_MIGRATED("스레드가 이동되었습니다"),
    POST_HIDDEN("게시글이 숨김 처리되었습니다"),

    MENTION("회원님이 언급되었습니다"),
    USER_BANNED("계정이 제재되었습니다"),

    BOARD_RECOMMEND("게시판이 추천되었습니다"),
    BOARD_ARCHIVED("게시판이 아카이브되었습니다"),
    BOARD_STATUS_CHANGED("게시판 상태가 변경되었습니다"),

    SYSTEM_MAINTENANCE("시스템 점검 안내입니다"),
    SYSTEM_CONFIG_CHANGED("시스템 설정이 변경되었습니다");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}