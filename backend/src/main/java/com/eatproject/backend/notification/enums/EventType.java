package com.eatproject.backend.notification.enums;

public enum EventType {

    // USER
    COMMENT,
    REPLY,
    LIKE,
    QUOTE,

    // SYSTEM
    BOARD_RECOMMEND,
    THREAD_LOCKED,
    BOARD_ARCHIVE,
    LIKE_BULK,
    TRENDING,

    // ADMIN
    ROLE_CHANGED,
    BOARD_APPROVED,
    BOARD_HIDDEN,
    ACCOUNT_BANNED,
    ACCOUNT_UNBANNED,
    POST_REMOVED,
    CHAT_APPROVED,

    // SYSTEM NOTICE
    SYSTEM_MAINTENANCE,
    SYSTEM_NOTICE,

    // ETC
    ANON_CHANGED
}