package com.eatproject.backend.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;   // 성공 여부
    private String message;    // 메시지
    private T result;          // 실제 데이터
    private int page;          // 페이지 번호 (페이징 시)
    private long totalNumber;  // 총 데이터 개수 (페이징 시)

    // 단순 성공 응답을 위한 정적 메서드
    public static <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("성공하였습니다.")
                .result(result)
                .build();
    }
}