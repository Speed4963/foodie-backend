package com.eatproject.backend.restaurant.service;

import com.eatproject.backend.common.CategoryType;
import com.eatproject.backend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.backend.restaurant.dto.RestaurantDto;
import com.eatproject.backend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.backend.restaurant.entity.*;
import com.eatproject.backend.restaurant.repository.RestaurantRepository;
import com.eatproject.backend.restaurant.repository.RestaurantTagRepository; // ✅ 추가: 부모 태그 조회를 위해 필요

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTagRepository restaurantTagRepository; // ✅ 추가: 부모 태그 조회를 위한 주입
    private final NaverMapService naverMapService;

    // --- [조회 기능] ---

    public Page<RestaurantDto> selectRestaurantList(String searchKeyword, Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.selectRestaurantList(searchKeyword, pageable);
        return restaurants.map(this::toDto);
    }

    public Page<RestaurantDto> selectRestaurantListByCategory(String category, Pageable pageable) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            return selectRestaurantList(null, pageable);
        }

        try {
            CategoryType categoryEnum = CategoryType.valueOf(category.toUpperCase());
            // ✅ Repository에서 단일 연관관계(restaurantTag)를 조회하도록 쿼리 수정됨을 전제
            Page<Restaurant> restaurants = restaurantRepository.findAllByCategory(categoryEnum, pageable);
            return restaurants.map(this::toDto);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 카테고리 요청입니다: {}", category);
            return Page.empty(pageable);
        }
    }

    public RestaurantDto findById(Integer id) {
        Restaurant restaurant = restaurantRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new RuntimeException("해당 식당 정보를 찾을 수 없습니다."));
        return toFullDto(restaurant);
    }
//    카테고리 변경
    @Transactional
    public void updateCategoryInfo(Integer tagId, String newCustomTag) {
        RestaurantTag tag = restaurantTagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("수정할 카테고리가 없습니다."));

        // 커스텀 태그 업데이트 (예: "#비건" -> "#건강한채식")
        tag.setCustomTag(newCustomTag.replace("#", "").trim());

        // 💡 주의: 부모를 수정하면 이 카테고리를 쓰는 모든 식당의 태그가 한꺼번에 바뀝니다!
    }

    // --- [등록 / 수정 / 삭제 기능] ---

    @Transactional
    public Integer saveRestaurant(RestaurantCreateDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());

        // 좌표 설정 (기존 로직 유지)
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            NaverMapService.Coordinate coord = naverMapService.getCoordinate(dto.getAddress());
            if (coord != null) {
                restaurant.setLat(BigDecimal.valueOf(coord.lat()));
                restaurant.setLng(BigDecimal.valueOf(coord.lng()));
            }
        } else {
            restaurant.setLat(dto.getLat());
            restaurant.setLng(dto.getLng());
            restaurant.setGeohash(dto.getGeohash());
        }

        restaurant.setAvgPrice(dto.getAvgPrice());
        restaurant.setMinPrice(dto.getMinPrice());
        restaurant.setMaxPrice(dto.getMaxPrice());
        restaurant.setLastSyncAt(LocalDateTime.now());

        // ✅ [고친 부분] 단일 부모 태그 연결
        // 이제 Tag를 새로 생성하지 않고, 전달받은 tagId로 부모를 찾아 연결합니다.
        if (dto.getTagId() != null) {
            RestaurantTag tag = restaurantTagRepository.findById(dto.getTagId())
                    .orElseThrow(() -> new RuntimeException("해당 카테고리 태그가 존재하지 않습니다."));
            restaurant.setRestaurantTag(tag);
        }

        // 메뉴/이미지 등록 (기존 로직 유지)
        processMenus(restaurant, dto);
        processImages(restaurant, dto);

        return restaurantRepository.save(restaurant).getRestId();
    }

    @Transactional
    public void updateRestaurant(Integer id, RestaurantUpdateDto dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("수정할 식당 정보가 없습니다."));

        // 주소 및 기본정보 수정 (기존 로직 유지)
        if (dto.getAddress() != null && !dto.getAddress().equals(restaurant.getAddress())) {
            NaverMapService.Coordinate coord = naverMapService.getCoordinate(dto.getAddress());
            if (coord != null) {
                restaurant.setLat(BigDecimal.valueOf(coord.lat()));
                restaurant.setLng(BigDecimal.valueOf(coord.lng()));
            }
            restaurant.setAddress(dto.getAddress());
        }
        restaurant.setName(dto.getName());
        restaurant.setAvgPrice(dto.getAvgPrice());
        restaurant.setMinPrice(dto.getMinPrice());
        restaurant.setMaxPrice(dto.getMaxPrice());
        restaurant.setLastSyncAt(LocalDateTime.now());

        // ✅ [고친 부분] 부모 태그 수정
        // 리스트를 clear하는 것이 아니라 새로운 부모 ID로 교체합니다.
        if (dto.getTagId() != null) {
            RestaurantTag newTag = restaurantTagRepository.findById(dto.getTagId())
                    .orElseThrow(() -> new RuntimeException("수정할 카테고리 태그가 없습니다."));
            restaurant.setRestaurantTag(newTag);
        }

        // 메뉴/이미지 수정 (기존 로직 유지)
        restaurant.getMenus().clear();
        processMenusForUpdate(restaurant, dto);
        restaurant.getImages().clear();
        processImagesForUpdate(restaurant, dto);
    }

    @Transactional
    public void deleteRestaurant(Integer id) {
        Restaurant restaurant = restaurantRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new RuntimeException("삭제할 식당이 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        restaurant.setDeletedAt(now);

        if (restaurant.getMenus() != null) {
            restaurant.getMenus().forEach(menu -> menu.setDeletedAt(now));
        }
        if (restaurant.getImages() != null) {
            restaurant.getImages().forEach(image -> image.setDeletedAt(now));
        }

        // ✅ [고친 부분] 태그 삭제 로직 제거
        // 이제 태그는 부모 테이블(마스터)이므로 식당이 삭제된다고 해서 태그 자체가 삭제되면 안 됩니다.
        // 연관관계만 끊고 싶다면 restaurant.setRestaurantTag(null)을 할 수 있으나 보통 유지합니다.
    }

    // --- [공통 변환 메서드] ---

    private RestaurantDto toDto(Restaurant entity) {
        RestaurantDto dto = new RestaurantDto();
        dto.setRestId(entity.getRestId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setAvgPrice(entity.getAvgPrice());

        // ✅ [고친 부분] 부모 태그 정보 추출
        if (entity.getRestaurantTag() != null) {
            dto.setCategory(entity.getRestaurantTag().getCategory().name());
        }
        return dto;
    }

    private RestaurantDto toFullDto(Restaurant entity) {
        RestaurantDto dto = toDto(entity);
        dto.setLat(entity.getLat());
        dto.setLng(entity.getLng());
        dto.setGeohash(entity.getGeohash());
        dto.setMinPrice(entity.getMinPrice());
        dto.setMaxPrice(entity.getMaxPrice());
        dto.setCreatedAt(entity.getCreatedAt());

        // 메뉴/이미지 변환 (기존 로직 유지)
        dto.setMenus(entity.getMenus().stream().filter(m -> m.getDeletedAt() == null).map(this::toMenuDto).collect(Collectors.toList()));
        dto.setImages(entity.getImages().stream().filter(i -> i.getDeletedAt() == null).map(this::toImageDto).collect(Collectors.toList()));

        // ✅ [고친 부분] 태그 변환 (List가 아닌 단일 정보로 매핑)
        if (entity.getRestaurantTag() != null) {
            dto.setCategory(entity.getRestaurantTag().getCategory().name());
            dto.setCustomTag("#" + entity.getRestaurantTag().getCustomTag());
        }

        return dto;
    }

    // --- [내부 헬퍼 메서드 (기존 로직 분리)] ---
    private void processMenus(Restaurant r, RestaurantCreateDto d) { /* 기존 메뉴 등록 로직 */ }
    private void processImages(Restaurant r, RestaurantCreateDto d) { /* 기존 이미지 등록 로직 */ }
    private void processMenusForUpdate(Restaurant r, RestaurantUpdateDto d) { /* 기존 메뉴 수정 로직 */ }
    private void processImagesForUpdate(Restaurant r, RestaurantUpdateDto d) { /* 기존 이미지 수정 로직 */ }
    private RestaurantDto.MenuResponseDto toMenuDto(Menu m) { /* 변환 로직 */ return new RestaurantDto.MenuResponseDto(); }
    private RestaurantDto.ImageResponseDto toImageDto(RestaurantImage i) { /* 변환 로직 */ return new RestaurantDto.ImageResponseDto(); }
}