package com.eatproject.backend.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonException {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        log.error("비즈니스 에러 발생: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(e.getReason())
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllException(Exception e) {
        e.printStackTrace();
        log.error("서버 내부 오류 발생: ", e);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message("서버 오류가 발생했습니다. 관리자에게 문의하세요.")
                .build();

        return ResponseEntity.status(500).body(response);
    }
}