package com.eatproject.frontend.restaurant.controller;

import com.eatproject.frontend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.frontend.restaurant.dto.RestaurantDto;
import com.eatproject.frontend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.frontend.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // --- [유저/공통 기능] ---

    /**
     * 1. 식당 전체 목록 조회 (검색어 포함, 페이징)
     * 예시: GET /api/restaurants?searchKeyword=치킨&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<RestaurantDto>> getRestaurantList(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RestaurantDto> list = restaurantService.selectRestaurantList(searchKeyword, pageable);
        return ResponseEntity.ok(list);
    }

    /**
     * 2. 카테고리별 식당 목록 조회 (페이징)
     * 예시: GET /api/restaurants/category/KOREAN?page=0&size=10
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<RestaurantDto>> getRestaurantListByCategory(
            @PathVariable("category") String category,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RestaurantDto> list = restaurantService.selectRestaurantListByCategory(category, pageable);
        return ResponseEntity.ok(list);
    }

    /**
     * 3. 식당 상세 정보 조회 (메뉴, 이미지 포함)
     * 예시: GET /api/restaurants/5
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable("id") Integer id) {
        RestaurantDto restaurant = restaurantService.findById(id);
        return ResponseEntity.ok(restaurant);
    }


    // --- [관리자 전용 기능] ---

    /**
     * 4. 식당 신규 등록
     * 예시: POST /api/restaurants (JSON 데이터 전달)
     */
    @PostMapping
    public ResponseEntity<Integer> createRestaurant(@RequestBody RestaurantCreateDto createDto) {
        Integer restId = restaurantService.saveRestaurant(createDto);
        return ResponseEntity.status(201).body(restId); // 201 Created 응답
    }

    /**
     * 5. 식당 정보 수정 (메뉴/이미지 포함)
     * 예시: PUT /api/restaurants/5
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable("id") Integer id,
            @RequestBody RestaurantUpdateDto updateDto) {

        restaurantService.updateRestaurant(id, updateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 6. 식당 정보 삭제 (Soft Delete)
     * 예시: DELETE /api/restaurants/5
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Integer id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build(); // 204 No Content 응답
    }
}