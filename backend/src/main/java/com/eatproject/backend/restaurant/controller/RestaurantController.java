package com.eatproject.backend.restaurant.controller;

import com.eatproject.backend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.backend.restaurant.dto.RestaurantDto;
import com.eatproject.backend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.backend.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Log4j2
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // --- [유저/공용 기능] ---

    @GetMapping
    public ResponseEntity<Page<RestaurantDto>> getRestaurantList(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("식당 목록 조회 - 검색어: {}, 페이지: {}", searchKeyword, pageable.getPageNumber());
        return ResponseEntity.ok(restaurantService.selectRestaurantList(searchKeyword, pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<RestaurantDto>> getRestaurantListByCategory(
            @PathVariable("category") String category,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("카테고리별 조회 - 카테고리: {}", category);
        return ResponseEntity.ok(restaurantService.selectRestaurantListByCategory(category, pageable));
    }

    @GetMapping("/{restId}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable("restId") Integer restId) {
        log.info("식당 상세 조회 - ID: {}", restId);
        return ResponseEntity.ok(restaurantService.findById(restId));
    }

    // --- [관리자 전용 기능] ---

    @PostMapping
    public ResponseEntity<Integer> createRestaurant(@Valid @RequestBody RestaurantCreateDto createDto) {
        log.info("식당 신규 등록 - Name: {}", createDto.getName());
        return ResponseEntity.status(201).body(restaurantService.saveRestaurant(createDto));
    }

    @PutMapping("/{restId}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable("restId") Integer restId,
            @Valid @RequestBody RestaurantUpdateDto updateDto) {
        log.info("식당 정보 수정 - ID: {}", restId);
        restaurantService.updateRestaurant(restId, updateDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("restId") Integer restId) {
        log.info("식당 삭제 요청 - ID: {}", restId);
        restaurantService.deleteRestaurant(restId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/categories/{tagId}")
    public ResponseEntity<Void> updateCategoryInfo(
            @PathVariable("tagId") Integer tagId,
            @RequestParam("customTag") String customTag) {
        log.info("카테고리 수정 - TagID: {}, NewTag: {}", tagId, customTag);
        restaurantService.updateCategoryInfo(tagId, customTag);
        return ResponseEntity.ok().build();
    }
    @Value("${image.upload-dir}")
    private String UPLOAD_DIR; // 서버 실제 경로

    @PostMapping("/images/upload")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                String originalName = file.getOriginalFilename();
                String fileName = UUID.randomUUID().toString();
                File dest = new File(UPLOAD_DIR + fileName);

                file.transferTo(dest);

                // 저장된 경로를 리스트에 추가
                fileUrls.add("/uploads/" + fileName);
            } catch (IOException e) {
                log.error("파일 저장 실패", e);
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.ok(fileUrls);
    }
    @GetMapping("/uploads/{uuid}")
    public ResponseEntity<byte[]> fileDownload(@PathVariable String uuid) {
        try {
            // 1) 지정된 업로드 경로와 uuid(파일명)를 조합하여 최종 파일 경로 생성
            Path filePath = Paths.get(UPLOAD_DIR, uuid);

            // 파일 존재 여부 검증 (파일이 없으면 404 Not Found 반환)
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 2) 서버 PC에 있는 이미지를 byte 배열로 읽어오기
            byte[] file = Files.readAllBytes(filePath);

            // 3) 헤더 설정: 웹 브라우저에 다운로드 창을 띄우도록 첨부파일(attachment) 지정
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(uuid, StandardCharsets.UTF_8)
                    .build();

            // 4) 클라이언트(웹 브라우저)로 전송
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // 문서종류: 이진파일
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .body(file);

        } catch (Exception e) {
            e.printStackTrace();
            // 파일을 읽는 중 에러가 발생하면 500 에러 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    }
