package com.eatproject.backend.notification.entity;



public enum NotificationType {

    COMMENT_CREATED,
    POST_LIKED,
    MENTION,

    THREAD_LOCKED,
    THREAD_MIGRATED,

    BOARD_ARCHIVED,
    BOARD_STATUS_CHANGED,

    BOARD_RECOMMEND,
    SYSTEM_MAINTENANCE
}