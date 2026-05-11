package com.eatproject.backend.trafficstats.service;

import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.board.repository.BoardRepository;
import com.eatproject.backend.posts.entity.Post;
import com.eatproject.backend.posts.repository.PostRepository;
import com.eatproject.backend.trafficstats.dto.TrafficStatsDto;
import com.eatproject.backend.trafficstats.dto.TrafficStatsResponseDto;
import com.eatproject.backend.trafficstats.entity.TrafficStats;
import com.eatproject.backend.trafficstats.repository.TrafficStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class TrafficStatsService {

    private final TrafficStatsRepository trafficStatsRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;


//      [통계 생성 핵심 로직]
//      특정 날짜의 모든 게시판 글을 분석하여 키워드 빈도를 집계하고 저장합니다.
//      @param targetDate 집계 대상 날짜


    @Transactional // 쓰기 작업이 포함되므로 별도의 트랜잭션 설정
    public void generateAndSaveStats(LocalDate targetDate) {
        log.info("=== TrafficStats 집계 시작: {} ===", targetDate);

        // 1. 중복 집계 방지: 해당 날짜에 이미 집계된 기존 데이터가 있다면 삭제 (Delete-then-Insert)
        trafficStatsRepository.deleteByStatDate(targetDate);

        // 2. 집계 시간 범위 설정 (대상 날짜의 00:00:00 ~ 23:59:59)
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        // 3. 시스템 내의 모든 게시판 목록을 가져옴
        List<Board> boards = boardRepository.findAll();

        for (Board board : boards) {
            // 4. 해당 게시판에서 지정된 시간 범위 내에 작성된 모든 게시글 조회
            List<Post> posts = postRepository.findAllByBoard_BoardIdAndCreatedAtBetween(
                    board.getBoardId(), start, end);

            // 게시글이 없는 게시판은 건너뜀
            if (posts.isEmpty()) continue;

            // 5. 게시글 리스트로부터 키워드와 빈도수를 추출 (Map 형태)
            Map<String, Integer> keywordMap = extractKeywordsFromPosts(posts);

            // 6. 유의미한 데이터 필터링 및 엔티티 변환
            List<TrafficStats> statsList = keywordMap.entrySet().stream()
                    .filter(entry -> entry.getValue() >= 1) // 언급 횟수가 1회 이상인 키워드만 선택
                    .map(entry -> TrafficStats.builder()
                            .board(board)
                            .keyword(entry.getKey())
                            .mentionCount(entry.getValue())
                            .statDate(targetDate)
                            .build())
                    .toList();

            // 7. 필터링된 키워드 통계 데이터를 DB에 일괄 저장
            if (!statsList.isEmpty()) {
                trafficStatsRepository.saveAll(statsList);
            }
        }
        log.info("=== TrafficStats 집계 완료: {} ===", targetDate);
    }


//      [관리자용 단일 날짜 조회]
//      특정 게시판의 특정 날짜 키워드 통계를 가져옵니다.

    public List<TrafficStatsDto> getStatsForAdmin(Integer boardId, LocalDate statDate) {
        return trafficStatsRepository.findAllByBoardIdAndStatDate(boardId, statDate).stream()
                .map(ts -> TrafficStatsDto.builder()
                        .boardName(ts.getBoard().getName())
                        .keyword(ts.getKeyword())
                        .mentionCount(ts.getMentionCount().longValue())
                        .statDate(statDate)
                        .build())
                .toList();
    }

    /**
     * [관리자용 기간별 조회]
     * 특정 기간(시작일~종료일) 동안의 키워드 빈도 합계를 조회합니다.
     */
    // 기간 조회 시 (TrafficStatsResponseDto 사용)
    public List<TrafficStatsResponseDto> getStatsByPeriod(Integer boardId, LocalDate startDate, LocalDate endDate) {
        return trafficStatsRepository.findStatsByPeriod(boardId, startDate, endDate);
    }


//      [내부 로직] 게시글 본문에서 단어를 분리하고 빈도수를 측정합니다.
//     @param posts 분석할 게시글 리스트
//      @return 키워드별 출현 횟수가 담긴 Map
//      집계 기준: 명상 + 형용사, 3회이상 언급되야 집계시작

    private Map<String, Integer> extractKeywordsFromPosts(List<Post> posts) {
        Map<String, Integer> counts = new HashMap<>();

        // 1. 수집하고 싶은 '한 글자' 핵심 단어들을 미리 정의 (사전 역할)
        // 자바 17의 Set.of를 사용하여 빠르고 메모리 효율적으로 관리합니다.
        final Set<String> FOOD_WORDS = Set.of("떡", "밥", "죽", "회", "닭", "면", "게", "굴", "술", "빵", "찜", "탕");

        for (Post post : posts) {
            String content = post.getContent();
            if (content == null || content.isBlank()) continue;

            // 2. 특수문자 제거 및 공백 기준 분리
            // "떡!!" -> "떡"으로 정제하여 정확도를 높입니다.
            String cleanContent = content.replaceAll("[^a-zA-Z가-힣\\s]", " ");
            String[] tokens = cleanContent.split("\\s+");

            for (String token : tokens) {
                // 3. 필터링 조건
                // - 길이가 2글자 이상이거나 (맛집, 마라탕 등)
                // - 1글자인데 우리가 지정한 음식 관련 단어인 경우 (떡, 회 등)
                if (token.length() >= 2 || FOOD_WORDS.contains(token)) {
                    counts.put(token, counts.getOrDefault(token, 0) + 1);
                }
            }
        }
        return counts;
    }
}