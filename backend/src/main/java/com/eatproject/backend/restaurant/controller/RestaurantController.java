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

    /**
     * 1. 식당 전체 조회 (검색 및 페이징)
     */
    @GetMapping
    public ResponseEntity<Page<RestaurantDto>> getRestaurantList(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RestaurantDto> list = restaurantService.selectRestaurantList(searchKeyword, pageable);
        return ResponseEntity.ok(list);
    }

    /**
     * 2. 카테고리별 식당 목록 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<RestaurantDto>> getRestaurantListByCategory(
            @PathVariable("category") String category,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RestaurantDto> list = restaurantService.selectRestaurantListByCategory(category, pageable);
        return ResponseEntity.ok(list);
    }

    /**
     * 3. 식당 단건 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable("id") Integer id) {
        RestaurantDto restaurant = restaurantService.findById(id);
        return ResponseEntity.ok(restaurant);
    }


    // --- [관리자 전용 기능] ---

    /**
     * 4. 식당 신규 등록
     * 이제 DTO 내부의 tagId를 통해 부모 카테고리와 연결됩니다.
     */
    @PostMapping
    public ResponseEntity<Integer> createRestaurant(@Valid @RequestBody RestaurantCreateDto createDto) {
        log.info("식당 등록 요청: {}", createDto.getName());
        Integer restId = restaurantService.saveRestaurant(createDto);
        return ResponseEntity.status(201).body(restId);
    }

    /**
     * 5. 식당 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable("id") Integer id,
            @Valid @RequestBody RestaurantUpdateDto updateDto) {

        log.info("식당 수정 요청 - ID: {}, Name: {}", id, updateDto.getName());
        restaurantService.updateRestaurant(id, updateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 6. 식당 삭제 (Soft Delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Integer id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 7. [추가] 카테고리 마스터 정보 수정
     * 식당 정보와 별개로, 부모 테이블인 RESTAURANT_TAGS의 정보를 수정합니다.
     */
    @PutMapping("/categories/{tagId}")
    public ResponseEntity<Void> updateCategoryInfo(
            @PathVariable("tagId") Integer tagId,
            @RequestParam("customTag") String customTag) {

        log.info("카테고리 정보 수정 요청 - TagID: {}, NewTag: {}", tagId, customTag);
        restaurantService.updateCategoryInfo(tagId, customTag);
        return ResponseEntity.ok().build();
    }
}