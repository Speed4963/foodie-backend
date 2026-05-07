package com.eatproject.backend.restaurant.service;

import com.eatproject.backend.common.CategoryType;
import com.eatproject.backend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.backend.restaurant.dto.RestaurantDto;
import com.eatproject.backend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.backend.restaurant.entity.RestaurantTag;
import com.eatproject.backend.restaurant.entity.Menu;
import com.eatproject.backend.restaurant.entity.Restaurant;
import com.eatproject.backend.restaurant.entity.RestaurantImage;
import com.eatproject.backend.restaurant.repository.RestaurantRepository;

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
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // --- [조회 기능] ---

    /**
     * 1. 전체 목록 조회 (키워드 검색 + 페이징)
     */
    public Page<RestaurantDto> selectRestaurantList(String searchKeyword, Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.selectRestaurantList(searchKeyword, pageable);
        return restaurants.map(this::toDto);
    }

    /**
     * 2. 카테고리별 목록 조회 (페이징)
     */
    /**
     * 2. 카테고리별 목록 조회 (전체 보기 지원)
     */
    public Page<RestaurantDto> selectRestaurantListByCategory(String category, Pageable pageable) {
        // 1. 카테고리 값이 없거나 "ALL"인 경우 -> 전체 목록 조회로 토스!
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            log.info("카테고리 미지정 또는 ALL 요청: 전체 목록을 조회합니다.");
            return selectRestaurantList(null, pageable); // 기존 전체 조회 메서드 활용
        }

        // 2. 특정 카테고리가 지정된 경우 -> ENUM 변환 후 필터링
        try {
            CategoryType categoryEnum = CategoryType.valueOf(category.toUpperCase());
            Page<Restaurant> restaurants = restaurantRepository.findAllByCategory(categoryEnum, pageable);

            return restaurants.map(this::toDto);
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리 값이 들어온 경우 에러 로그를 남기고 빈 페이지 반환
            log.error("잘못된 카테고리 요청입니다: {}", category);
            return Page.empty(pageable);
        }
    }

    /**
     * 3. 단일 상세 조회 (ID 기준 + 모든 정보 Fetch Join)
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

        // 메뉴 등록
        if (dto.getMenus() != null) {
            boolean anyRepProvided = dto.getMenus().stream()
                    .anyMatch(m -> m.getIsRepresentative() != null && m.getIsRepresentative());

            for (int i = 0; i < dto.getMenus().size(); i++) {
                var menuDto = dto.getMenus().get(i);
                Menu menu = new Menu();
                menu.setPName(menuDto.getPName());
                menu.setPrice(menuDto.getPrice());

                if (menuDto.getIsRepresentative() != null && menuDto.getIsRepresentative()) {
                    menu.setIsRepresentative(true);
                } else if (i == 0 && !anyRepProvided) {
                    menu.setIsRepresentative(true);
                } else {
                    menu.setIsRepresentative(false);
                }
                restaurant.addMenu(menu);
            }
        }

        // 이미지 등록
        if (dto.getImages() != null) {
            boolean anyThumbProvided = dto.getImages().stream()
                    .anyMatch(img -> img.getThumbUrl() != null && !img.getThumbUrl().isEmpty());

            for (int i = 0; i < dto.getImages().size(); i++) {
                var imgDto = dto.getImages().get(i);
                RestaurantImage image = new RestaurantImage();
                image.setImgUrl(imgDto.getImgUrl());
                image.setDisplayOrder(i);
                image.setCategory(imgDto.getCategory());

                if (imgDto.getThumbUrl() != null && !imgDto.getThumbUrl().isEmpty()) {
                    image.setThumbUrl(imgDto.getThumbUrl());
                } else if (i == 0 && !anyThumbProvided) {
                    image.setThumbUrl(imgDto.getImgUrl());
                } else {
                    image.setThumbUrl(null);
                }
                restaurant.addImage(image);
            }
        }

        // 태그 등록 (TagCreateDto 구조 반영)
        if (dto.getCustomTags() != null) {
            dto.getCustomTags().forEach(tagDto -> {
                RestaurantTag tag = new RestaurantTag();
                tag.setCustomTag(tagDto.getCustomTag().replace("#", "").trim()); // setTagName -> setCustomTag 수정
                restaurant.addRestaurantTag(tag);
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

        restaurant.setName(dto.getName());
        restaurant.setLat(dto.getLat());
        restaurant.setLng(dto.getLng());
        restaurant.setAvgPrice(dto.getAvgPrice());
        restaurant.setMinPrice(dto.getMinPrice());
        restaurant.setMaxPrice(dto.getMaxPrice());
        restaurant.setLastSyncAt(LocalDateTime.now());

        // 메뉴 수정
        if (dto.getMenus() != null) {
            restaurant.getMenus().clear();
            boolean anyRepProvided = dto.getMenus().stream()
                    .anyMatch(m -> m.getIsRepresentative() != null && m.getIsRepresentative());

            for (int i = 0; i < dto.getMenus().size(); i++) {
                var menuDto = dto.getMenus().get(i);
                Menu menu = new Menu();
                menu.setPName(menuDto.getPName());
                menu.setPrice(menuDto.getPrice());

                if (menuDto.getIsRepresentative() != null && menuDto.getIsRepresentative()) {
                    menu.setIsRepresentative(true);
                } else if (i == 0 && !anyRepProvided) {
                    menu.setIsRepresentative(true);
                } else {
                    menu.setIsRepresentative(false);
                }
                restaurant.addMenu(menu);
            }
        }

        // 이미지 수정
        if (dto.getImages() != null) {
            restaurant.getImages().clear();
            boolean anyThumbProvided = dto.getImages().stream()
                    .anyMatch(img -> img.getThumbUrl() != null && !img.getThumbUrl().isEmpty());

            for (int i = 0; i < dto.getImages().size(); i++) {
                var imgDto = dto.getImages().get(i);
                RestaurantImage image = new RestaurantImage();
                image.setImgUrl(imgDto.getImgUrl());
                image.setDisplayOrder(i);
                image.setCategory(imgDto.getCategory());

                if (imgDto.getThumbUrl() != null && !imgDto.getThumbUrl().isEmpty()) {
                    image.setThumbUrl(imgDto.getThumbUrl());
                } else if (i == 0 && !anyThumbProvided) {
                    image.setThumbUrl(imgDto.getImgUrl());
                } else {
                    image.setThumbUrl(null);
                }
                restaurant.addImage(image);
            }
        }

        // 태그 수정 (getTags -> getRestaurantTags 수정)
        if (dto.getCustomTags() != null) {
            restaurant.getTags().clear();
            dto.getCustomTags().forEach(tagDto -> {
                RestaurantTag tag = new RestaurantTag();
                tag.setCustomTag(tagDto.getCustomTag().replace("#", "").trim()); // setTagName -> setCustomTag 수정
                restaurant.addRestaurantTag(tag);
            });
        }
    }

    /**
     * 6. 식당 삭제 (Soft Delete)
     */
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

        // 2) 태그는 DB에 deleted_at이 없으므로 실제로 데이터 삭제 (Hard Delete)
        if (restaurant.getTags() != null) {
            restaurant.getTags().clear(); // 바구니를 비우면 DB에서 즉시 삭제됩니다.
        }
    }


    // --- [공통 변환 메서드] ---

    private RestaurantDto toDto(Restaurant entity) {
        RestaurantDto dto = new RestaurantDto();
        dto.setRestId(entity.getRestId());
        dto.setName(entity.getName());
        dto.setAvgPrice(entity.getAvgPrice());
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

        // 메뉴 변환
        dto.setMenus(entity.getMenus().stream()
                .filter(m -> m.getDeletedAt() == null)
                .map(m -> {
                    RestaurantDto.MenuResponseDto mDto = new RestaurantDto.MenuResponseDto();
                    mDto.setMenuId(m.getMenuId());
                    mDto.setPName(m.getPName());
                    mDto.setPrice(m.getPrice());
                    mDto.setIsRepresentative(m.getIsRepresentative());
                    return mDto;
                }).collect(Collectors.toList()));

        // 이미지 변환
        dto.setImages(entity.getImages().stream()
                .filter(i -> i.getDeletedAt() == null)
                .map(i -> {
                    RestaurantDto.ImageResponseDto iDto = new RestaurantDto.ImageResponseDto();
                    iDto.setImgId(i.getImgId());
                    iDto.setImgUrl(i.getImgUrl());
                    iDto.setThumbUrl(i.getThumbUrl());
                    iDto.setCategory(i.getCategory());
                    return iDto;
                }).collect(Collectors.toList()));

        // 태그 조회 변환 (t.getTagName -> t.getCustomTag 수정)
        if (entity.getTags() != null) {
            dto.setTags(entity.getTags().stream()
                    .map(t -> {
                        RestaurantDto.TagResponseDto tagDto = new RestaurantDto.TagResponseDto();
                        tagDto.setCategory(t.getCategory());
                        tagDto.setCustomTag("#" + t.getCustomTag());
                        return tagDto;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}