package com.eatproject.backend.restaurant.controller;

import com.eatproject.backend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.backend.restaurant.dto.RestaurantDto;
import com.eatproject.backend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.backend.restaurant.service.RestaurantService;
import jakarta.validation.Valid; // 추가
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
//식당 전체조회
    @GetMapping
    public ResponseEntity<Page<RestaurantDto>> getRestaurantList(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RestaurantDto> list = restaurantService.selectRestaurantList(searchKeyword, pageable);
        return ResponseEntity.ok(list);
    }

    /**
     * 2. 카테고리별 식당 목록 조회
     * 서비스에서 "ALL"이나 잘못된 값 처리를 다 해주므로 String으로 받는 것이 가장 유연합니다.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<RestaurantDto>> getRestaurantListByCategory(
            @PathVariable("category") String category,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RestaurantDto> list = restaurantService.selectRestaurantListByCategory(category, pageable);
        return ResponseEntity.ok(list);
    }
//식당단건조회
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantDetail(@PathVariable("id") Integer id) {
        RestaurantDto restaurant = restaurantService.findById(id);
        return ResponseEntity.ok(restaurant);
    }


    // --- [관리자 전용 기능] ---

    /**
     * 4. 식당 신규 등록
     * @Valid를 추가하여 DTO에 설정한 제약 조건(@NotBlank 등)을 체크합니다.
     */
    @PostMapping
    public ResponseEntity<Integer> createRestaurant(@Valid @RequestBody RestaurantCreateDto createDto) {
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

        restaurantService.updateRestaurant(id, updateDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Integer id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}