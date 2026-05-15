package com.eatproject.backend.notification.event;

import com.eatproject.backend.notification.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class ActionEvent {

    private EventType type;      // 어떤 이벤트인지
    private String actor;        // 발생자 (USER or SYSTEM or ADMIN)
    private String targetEmail;  // 알림 대상
    private Long postId;         // 관련 게시글
    private Integer boardId;     // 관련 게시판
    private String message;      // 추가 메시지
}