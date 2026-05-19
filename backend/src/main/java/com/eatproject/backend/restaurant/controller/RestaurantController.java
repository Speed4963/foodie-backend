package com.eatproject.backend.restaurant.controller;

import com.eatproject.backend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.backend.restaurant.dto.RestaurantDto;
import com.eatproject.backend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.backend.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable("id") Integer id) {
        log.info("식당 상세 조회 - ID: {}", id);
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    // --- [관리자 전용 기능] ---

    @PostMapping
    public ResponseEntity<Integer> createRestaurant(@Valid @RequestBody RestaurantCreateDto createDto) {
        log.info("식당 신규 등록 - Name: {}", createDto.getName());
        return ResponseEntity.status(201).body(restaurantService.saveRestaurant(createDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable("id") Integer id,
            @Valid @RequestBody RestaurantUpdateDto updateDto) {
        log.info("식당 정보 수정 - ID: {}", id);
        restaurantService.updateRestaurant(id, updateDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Integer id) {
        log.info("식당 삭제 요청 - ID: {}", id);
        restaurantService.deleteRestaurant(id);
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
}