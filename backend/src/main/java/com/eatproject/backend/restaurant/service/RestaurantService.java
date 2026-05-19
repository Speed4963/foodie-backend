package com.eatproject.backend.restaurant.service;

import com.eatproject.backend.common.CategoryType;
import com.eatproject.backend.restaurant.dto.RestaurantCreateDto;
import com.eatproject.backend.restaurant.dto.RestaurantDto;
import com.eatproject.backend.restaurant.dto.RestaurantUpdateDto;
import com.eatproject.backend.restaurant.entity.Menu;
import com.eatproject.backend.restaurant.entity.Restaurant;
import com.eatproject.backend.restaurant.entity.RestaurantImage;
import com.eatproject.backend.restaurant.entity.RestaurantTag;
import com.eatproject.backend.restaurant.repository.RestaurantRepository;
import com.eatproject.backend.restaurant.repository.RestaurantTagRepository;
import com.eatproject.backend.restaurant.service.NaverMapService;
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
    private final RestaurantTagRepository restaurantTagRepository;
    private final NaverMapService naverMapService;

    // --- [조회 기능] ---

    public Page<RestaurantDto> selectRestaurantList(String searchKeyword, Pageable pageable) {
        return restaurantRepository.selectRestaurantList(searchKeyword, pageable).map(e -> {
            RestaurantDto dto = new RestaurantDto();
            dto.setRestId(e.getRestId());
            dto.setName(e.getName());
            dto.setAddress(e.getAddress());
            dto.setAvgPrice(e.getAvgPrice());
            dto.setPhone(e.getPhone());
            dto.setBusinessHours(e.getBusinessHours());
            dto.setClosedDays(e.getClosedDays());
            if (e.getRestaurantTag() != null) dto.setCategory(e.getRestaurantTag().getCategory().name());
            return dto;
        });
    }

    public Page<RestaurantDto> selectRestaurantListByCategory(String category, Pageable pageable) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            return selectRestaurantList(null, pageable);
        }
        try {
            CategoryType categoryEnum = CategoryType.valueOf(category.toUpperCase());
            return restaurantRepository.findAllByCategory(categoryEnum, pageable).map(e -> {
                RestaurantDto dto = new RestaurantDto();
                dto.setRestId(e.getRestId());
                dto.setName(e.getName());
                dto.setAddress(e.getAddress());
                dto.setAvgPrice(e.getAvgPrice());
                dto.setPhone(e.getPhone());
                dto.setBusinessHours(e.getBusinessHours());
                dto.setClosedDays(e.getClosedDays());
                if (e.getRestaurantTag() != null) dto.setCategory(e.getRestaurantTag().getCategory().name());
                return dto;
            });
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }
    }

        public RestaurantDto findById(Integer id) {
            Restaurant e = restaurantRepository.findByIdWithAllDetails(id)
                    .orElseThrow(() -> new RuntimeException("해당 식당 정보를 찾을 수 없습니다."));

            RestaurantDto dto = new RestaurantDto();
            dto.setRestId(e.getRestId());
            dto.setName(e.getName());
            dto.setAddress(e.getAddress());
            dto.setLat(e.getLat());
            dto.setLng(e.getLng());
            dto.setGeohash(e.getGeohash());
            dto.setAvgPrice(e.getAvgPrice());
            dto.setMinPrice(e.getMinPrice());
            dto.setMaxPrice(e.getMaxPrice());
            dto.setDescription(e.getDescription());
            dto.setPhone(e.getPhone());
            dto.setBusinessHours(e.getBusinessHours());
            dto.setClosedDays(e.getClosedDays());
            dto.setSnsUrl(e.getSnsUrl());
            dto.setCreatedAt(e.getCreatedAt());

            if (e.getRestaurantTag() != null) {
                dto.setCategory(e.getRestaurantTag().getCategory().name());
                dto.setCustomTag("#" + e.getRestaurantTag().getCustomTag());
            }

            dto.setMenus(e.getMenus().stream().filter(m -> m.getDeletedAt() == null).map(m ->
                    RestaurantDto.MenuResponseDto.builder().menuId(m.getMenuId()).pName(m.getPName()).price(m.getPrice()).isRepresentative(m.getIsRepresentative()).build()
            ).collect(Collectors.toList()));

            dto.setImages(e.getImages().stream().filter(i -> i.getDeletedAt() == null).map(i ->
                    RestaurantDto.ImageResponseDto.builder().imgId(i.getImgId()).imgUrl(i.getImgUrl()).category(i.getCategory()).isMain(i.getIsMain()).build()
            ).collect(Collectors.toList()));

            return dto;
        }

    // --- [등록 / 수정 / 삭제] ---

    @Transactional
    public Integer saveRestaurant(RestaurantCreateDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());

        NaverMapService.Coordinate coord = (dto.getAddress() != null && !dto.getAddress().isBlank()) ? naverMapService.getCoordinate(dto.getAddress()) : null;
        restaurant.setLat(coord != null ? BigDecimal.valueOf(coord.lat()) : (dto.getLat() != null ? dto.getLat() : BigDecimal.ZERO));
        restaurant.setLng(coord != null ? BigDecimal.valueOf(coord.lng()) : (dto.getLng() != null ? dto.getLng() : BigDecimal.ZERO));
        restaurant.setGeohash(dto.getGeohash() != null ? dto.getGeohash() : "0000000000");

        restaurant.setAvgPrice(dto.getAvgPrice());
        restaurant.setMinPrice(dto.getMinPrice());
        restaurant.setMaxPrice(dto.getMaxPrice());
        restaurant.setDescription(dto.getDescription());
        restaurant.setPhone(dto.getPhone());
        restaurant.setBusinessHours(dto.getBusinessHours());
        restaurant.setClosedDays(dto.getClosedDays());
        restaurant.setSnsUrl(dto.getSnsUrl());
        restaurant.setLastSyncAt(LocalDateTime.now());

        if (dto.getTagId() != null) {
            restaurant.setRestaurantTag(restaurantTagRepository.findById(dto.getTagId()).orElseThrow());
        }

        if (dto.getMenus() != null) dto.getMenus().forEach(m -> {
            Menu menu = new Menu();
            menu.setPName(m.getPName()); menu.setPrice(m.getPrice()); menu.setIsRepresentative(m.getIsRepresentative() != null ? m.getIsRepresentative() : false);
            restaurant.addMenu(menu);
        });

        if (dto.getImages() != null) dto.getImages().forEach(i -> {
            RestaurantImage img = new RestaurantImage();
            img.setImgUrl(i.getImgUrl()); img.setThumbUrl(i.getThumbUrl()); img.setCategory(i.getCategory() != null ? i.getCategory() : "GENERAL");
            img.setDisplayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0); img.setIsMain(i.getIsMain() != null ? i.getIsMain() : false);
            restaurant.addImage(img);
        });

        return restaurantRepository.save(restaurant).getRestId();
    }
    public void updateCategoryInfo(Integer tagId, String newCustomTag) {
        // 1. 태그를 찾고, 없으면 에러 처리
        RestaurantTag tag = restaurantTagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("해당 카테고리 태그가 존재하지 않습니다."));

        // 2. 새로운 태그 이름으로 업데이트 (공백 제거 및 '#' 처리)
        tag.setCustomTag(newCustomTag.replace("#", "").trim());
    }

    @Transactional
    public void updateRestaurant(Integer id, RestaurantUpdateDto dto) {
        Restaurant r = restaurantRepository.findById(id).orElseThrow();
        r.setName(dto.getName());
        r.setAvgPrice(dto.getAvgPrice());
        r.setMinPrice(dto.getMinPrice());
        r.setMaxPrice(dto.getMaxPrice());
        r.setDescription(dto.getDescription());
        r.setPhone(dto.getPhone());
        r.setBusinessHours(dto.getBusinessHours());
        r.setClosedDays(dto.getClosedDays());
        r.setSnsUrl(dto.getSnsUrl());
        r.setLastSyncAt(LocalDateTime.now());

        r.getMenus().clear();
        if (dto.getMenus() != null) dto.getMenus().forEach(m -> {
            Menu menu = new Menu();
            menu.setPName(m.getPName()); menu.setPrice(m.getPrice()); menu.setIsRepresentative(m.getIsRepresentative() != null ? m.getIsRepresentative() : false);
            r.addMenu(menu);
        });

        r.getImages().clear();
        if (dto.getImages() != null) dto.getImages().forEach(i -> {
            RestaurantImage img = new RestaurantImage();
            img.setImgUrl(i.getImgUrl()); img.setThumbUrl(i.getThumbUrl()); img.setCategory(i.getCategory() != null ? i.getCategory() : "GENERAL");
            img.setDisplayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0); img.setIsMain(i.getIsMain() != null ? i.getIsMain() : false);
            r.addImage(img);
        });
    }

    @Transactional
    public void deleteRestaurant(Integer id) {
        restaurantRepository.findByIdWithAllDetails(id).orElseThrow().setDeletedAt(LocalDateTime.now());
    }
}