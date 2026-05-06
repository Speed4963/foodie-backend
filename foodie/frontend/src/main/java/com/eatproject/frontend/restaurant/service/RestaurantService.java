package com.eatproject.frontend.restaurant.service;

import com.eatproject.frontend.restaurant.dto.*;
import com.eatproject.frontend.restaurant.entity.*;
import com.eatproject.frontend.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 조회 성능 최적화
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // --- [조회 기능] ---

    /**
     * 1. 전체 목록 조회 (키워드 검색 + 페이징)
     */
    public Page<RestaurantDto> selectRestaurantList(String searchKeyword, Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.selectRestaurantList(searchKeyword, pageable);
        return restaurants.map(this::toDto); // 엔티티를 DTO로 변환
    }

    /**
     * 2. 카테고리별 목록 조회 (페이징)
     */
    public Page<RestaurantDto> selectRestaurantListByCategory(String category, Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.findAllByCategory(category, pageable);
        return restaurants.map(this::toDto);
    }

    /**
     * 3. 단일 상세 조회 (ID 기준 + 모든 정보 Fetch Join)
     * 상세 페이지 진입 시 메뉴, 사진, 태그를 한 번에 가져옵니다.
     */
    public RestaurantDto findById(Integer id) {
        Restaurant restaurant = restaurantRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new RuntimeException("해당 식당 정보를 찾을 수 없습니다."));
        return toFullDto(restaurant);
    }


    // --- [등록 / 수정 / 삭제 기능] ---

    /**
     * 4. 식당 정보 등록 (Create)
     */
    @Transactional
    public Integer saveRestaurant(RestaurantCreateDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setLat(dto.getLat());
        restaurant.setLng(dto.getLng());
        restaurant.setGeohash(dto.getGeohash());
        restaurant.setAvgPrice(dto.getAvgPrice());
        restaurant.setMinPrice(dto.getMinPrice());
        restaurant.setMaxPrice(dto.getMaxPrice());
        restaurant.setLastSyncAt(LocalDateTime.now());

        // 메뉴 추가
        if (dto.getMenus() != null) {
            dto.getMenus().forEach(m -> {
                Menu menu = new Menu();
                menu.setPName(m.getPName());
                menu.setPrice(m.getPrice());
                menu.setIsRepresentative(m.getIsRepresentative());
                restaurant.addMenu(menu);
            });
        }

        // 이미지 추가
        if (dto.getImages() != null) {
            dto.getImages().forEach(i -> {
                RestaurantImage image = new RestaurantImage();
                image.setImgUrl(i.getImgUrl());
                image.setThumbUrl(i.getThumbUrl());
                image.setCategory(i.getCategory());
                image.setIsMain(i.getIsMain());
                image.setDisplayOrder(i.getDisplayOrder());
                restaurant.addImage(image);
            });
        }

        return restaurantRepository.save(restaurant).getRestId();
    }

    /**
     * 5. 식당 정보 수정 (Update)
     */
    @Transactional
    public void updateRestaurant(Integer id, RestaurantUpdateDto dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("수정할 식당 정보가 없습니다."));

        // 1. 기본 필드 업데이트
        restaurant.setName(dto.getName());
        restaurant.setLat(dto.getLat());
        restaurant.setLng(dto.getLng());
        restaurant.setAvgPrice(dto.getAvgPrice());
        restaurant.setMinPrice(dto.getMinPrice());
        restaurant.setMaxPrice(dto.getMaxPrice());
        restaurant.setLastSyncAt(LocalDateTime.now());

        // 2. 메뉴 수정 (기존 삭제 후 재등록)
        if (dto.getMenus() != null) {
            restaurant.getMenus().clear(); // 기존 메뉴 리스트를 비우면 DB에서도 삭제됨 (orphanRemoval)
            dto.getMenus().forEach(m -> {
                Menu menu = new Menu();
                menu.setPName(m.getPName());
                menu.setPrice(m.getPrice());
                menu.setIsRepresentative(m.getIsRepresentative());
                restaurant.addMenu(menu);
            });
        }

        // 3. 이미지 수정 (기존 삭제 후 재등록)
        if (dto.getImages() != null) {
            restaurant.getImages().clear(); // 기존 사진 리스트를 비움
            dto.getImages().forEach(i -> {
                RestaurantImage image = new RestaurantImage();
                image.setImgUrl(i.getImgUrl());
                image.setThumbUrl(i.getThumbUrl());
                image.setCategory(i.getCategory());
                image.setIsMain(i.getIsMain());
                image.setDisplayOrder(i.getDisplayOrder());
                restaurant.addImage(image);
            });
        }
    }

    /**
     * 6. 식당 삭제 (Delete - Soft Delete)
     */
    @Transactional
    public void deleteRestaurant(Integer id) {
        // Fetch Join으로 자식들까지 한 번에 땡겨와서 처리하는 게 성능상 좋습니다.
        Restaurant restaurant = restaurantRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new RuntimeException("삭제할 식당이 없습니다."));

        LocalDateTime now = LocalDateTime.now();

        // 1) 식당 본체 삭제 표시
        restaurant.setDeletedAt(now);

        // 2) 메뉴들도 전부 삭제 표시 (데이터는 남기되 날짜만 기록)
        if (restaurant.getMenus() != null) {
            restaurant.getMenus().forEach(menu -> menu.setDeletedAt(now));
        }

        // 3) 이미지들도 전부 삭제 표시
        if (restaurant.getImages() != null) {
            restaurant.getImages().forEach(image -> image.setDeletedAt(now));
        }
    }


    // --- [공통 변환 메서드] ---

    /**
     * 목록 조회용 가벼운 DTO 변환
     */
    private RestaurantDto toDto(Restaurant entity) {
        RestaurantDto dto = new RestaurantDto();
        dto.setRestId(entity.getRestId());
        dto.setName(entity.getName());
        dto.setAvgPrice(entity.getAvgPrice());
        return dto;
    }

    /**
     * 상세 조회용 전체 정보 포함 DTO 변환
     */
    private RestaurantDto toFullDto(Restaurant entity) {
        RestaurantDto dto = toDto(entity);
        dto.setLat(entity.getLat());
        dto.setLng(entity.getLng());
        dto.setGeohash(entity.getGeohash());
        dto.setMinPrice(entity.getMinPrice());
        dto.setMaxPrice(entity.getMaxPrice());
        dto.setCreatedAt(entity.getCreatedAt());

        // 메뉴 변환
        dto.setMenus(entity.getMenus().stream().map(m -> {
            RestaurantDto.MenuResponseDto mDto = new RestaurantDto.MenuResponseDto();
            mDto.setMenuId(m.getMenuId());
            mDto.setPName(m.getPName());
            mDto.setPrice(m.getPrice());
            mDto.setIsRepresentative(m.getIsRepresentative());
            return mDto;
        }).collect(Collectors.toList()));

        // 이미지 변환
        dto.setImages(entity.getImages().stream().map(i -> {
            RestaurantDto.ImageResponseDto iDto = new RestaurantDto.ImageResponseDto();
            iDto.setImgId(i.getImgId());
            iDto.setImgUrl(i.getImgUrl());
            iDto.setCategory(i.getCategory());
            iDto.setIsMain(i.getIsMain());
            return iDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}