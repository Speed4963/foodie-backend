package com.eatproject.backend.config;

import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardDataInitializer implements CommandLineRunner {

    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // DB에 게시판 데이터가 하나도 없을 때만 실행
        if (boardRepository.count() == 0) {
            List<String> boardNames = Arrays.asList(
                    "채식맛집", "채식 자유", "주류매장", "주류 자유",
                    "이국맛집", "이국 자유", "괴식맛집", "괴식 자유",
                    "유명셰프맛집", "유명셰프 자유", "미슐랭", "미슐랭 자유",
                    "키즈존", "키즈존 자유", "동물식당", "동물식당 자유"
            );

            for (int i = 0; i < boardNames.size(); i++) {
                Board board = Board.builder()
                        .name(boardNames.get(i))
                        .slug("board-" + (i + 1))
                        .status("ACTIVE") // BoardService에서 활성화된 게시판만 조회하므로 필수
                        .proposedBy("테스트") // MEMBER 테이블에 있는 실제 닉네임이어야 에러 안 남
                        .build();

                boardRepository.save(board);
            }

            System.out.println("=========================================");
            System.out.println("✅ [알림] 기본 게시판 16개가 자동 생성되었습니다!");
            System.out.println("=========================================");
        }
    }
}