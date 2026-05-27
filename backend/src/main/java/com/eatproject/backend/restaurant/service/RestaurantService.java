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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator; // ✅ Comparator 추가
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
// 클래스 레벨에 readOnly = true를 적용하여 단순 조회 메서드의 성능을 최적화합니다.
// 데이터 변경이 일어나는 메서드에는 별도로 @Transactional을 붙여 덮어씌웁니다.
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTagRepository restaurantTagRepository;
    private final NaverMapService naverMapService;

    // application.properties(또는 yml)에서 서버 IP 주소를 주입받아 이미지 URL 생성 시 사용합니다.
    @Value("${spring.ip}")
    private String springip;

    // ==========================================
    // --- [조회 기능] ---
    // ==========================================

    /**
     * 전체 식당 목록 조회 (검색어 포함 및 페이징 처리)
     */
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

            if (e.getRestaurantTag() != null) {
                dto.setCategory(e.getRestaurantTag().getCategory().name());
            }

            if (e.getImages() != null) {
                // ✅ 1. 유효 이미지 필터링 후 등록 순서(displayOrder)대로 오름차순 정렬
                List<RestaurantImage> validImages = e.getImages().stream()
                        .filter(i -> i.getDeletedAt() == null)
                        .sorted(Comparator.comparingInt(RestaurantImage::getDisplayOrder))
                        .collect(Collectors.toList());

                // ✅ 2. 정렬된 상태에서 0번째를 대표 이미지(isMain=true)로 지정
                dto.setImages(IntStream.range(0, validImages.size())
                        .mapToObj(i -> {
                            RestaurantImage img = validImages.get(i);
                            return RestaurantDto.ImageResponseDto.builder()
                                    .imgId(img.getImgId())
                                    .imgUrl(springip + img.getImgUrl())
                                    .category(img.getCategory())
                                    .isMain(i == 0)
                                    .build();
                        })
                        .collect(Collectors.toList())
                );
            }

            return dto;
        });
    }

    /**
     * 특정 카테고리별 식당 목록 조회 (페이징 처리)
     */
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

                if (e.getImages() != null) {
                    // ✅ 이미지 정렬 추가
                    List<RestaurantImage> validImages = e.getImages().stream()
                            .filter(i -> i.getDeletedAt() == null)
                            .sorted(Comparator.comparingInt(RestaurantImage::getDisplayOrder))
                            .collect(Collectors.toList());

                    dto.setImages(IntStream.range(0, validImages.size())
                            .mapToObj(i -> {
                                RestaurantImage img = validImages.get(i);
                                return RestaurantDto.ImageResponseDto.builder()
                                        .imgId(img.getImgId())
                                        .imgUrl(springip + img.getImgUrl())
                                        .category(img.getCategory())
                                        .isMain(i == 0)
                                        .build();
                            })
                            .collect(Collectors.toList())
                    );
                }

                return dto;
            });
        } catch (IllegalArgumentException e) {
            return Page.empty(pageable);
        }
    }

    /**
     * 식당 상세 정보 단건 조회
     */
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

        // ✅ 메뉴 정렬 로직 추가: menuId 오름차순(등록순)
        dto.setMenus(e.getMenus().stream()
                .filter(m -> m.getDeletedAt() == null)
                .sorted(Comparator.comparingLong(Menu::getMenuId)) // ✨ 이 부분 추가됨
                .map(m -> RestaurantDto.MenuResponseDto.builder()
                        .menuId(m.getMenuId())
                        .pName(m.getPName())
                        .price(m.getPrice())
                        .isRepresentative(m.getIsRepresentative())
                        .build())
                .collect(Collectors.toList()));

        if (e.getImages() != null) {
            // ✅ 이미지 정렬 추가
            List<RestaurantImage> validImages = e.getImages().stream()
                    .filter(i -> i.getDeletedAt() == null)
                    .sorted(Comparator.comparingInt(RestaurantImage::getDisplayOrder))
                    .collect(Collectors.toList());

            dto.setImages(IntStream.range(0, validImages.size())
                    .mapToObj(i -> {
                        RestaurantImage img = validImages.get(i);
                        return RestaurantDto.ImageResponseDto.builder()
                                .imgId(img.getImgId())
                                .imgUrl(springip + img.getImgUrl())
                                .category(img.getCategory())
                                .isMain(i == 0)
                                .build();
                    })
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // ==========================================
    // --- [등록 / 수정 / 삭제] ---
    // ==========================================

    @Transactional
    public Integer saveRestaurant(RestaurantCreateDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());

        NaverMapService.Coordinate coord = (dto.getAddress() != null && !dto.getAddress().isBlank())
                ? naverMapService.getCoordinate(dto.getAddress())
                : null;

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
            menu.setPName(m.getPName());
            menu.setPrice(m.getPrice());
            menu.setIsRepresentative(m.getIsRepresentative() != null ? m.getIsRepresentative() : false);
            restaurant.addMenu(menu);
        });

        if (dto.getImages() != null) dto.getImages().forEach(i -> {
            RestaurantImage img = new RestaurantImage();

            String finalUrl = i.getImgUrl();
            if (finalUrl != null && !finalUrl.startsWith("/api/restaurants/")) {
                finalUrl = "/api/restaurants" + (finalUrl.startsWith("/") ? "" : "/") + finalUrl;
            }

            img.setImgUrl(finalUrl);
            img.setThumbUrl(finalUrl);
            img.setCategory(i.getCategory() != null ? i.getCategory() : "GENERAL");
            img.setDisplayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0);
            img.setIsMain(i.getIsMain() != null ? i.getIsMain() : false);


            restaurant.addImage(img);
        });

        return restaurantRepository.save(restaurant).getRestId();
    }

    @Transactional
    public void updateCategoryInfo(Integer tagId, String newCustomTag) {
        RestaurantTag tag = restaurantTagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("해당 카테고리 태그가 존재하지 않습니다."));
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
            menu.setPName(m.getPName());
            menu.setPrice(m.getPrice());
            menu.setIsRepresentative(m.getIsRepresentative() != null ? m.getIsRepresentative() : false);
            r.addMenu(menu);
        });

        r.getImages().clear();
        if (dto.getImages() != null) dto.getImages().forEach(i -> {
            RestaurantImage img = new RestaurantImage();

            String finalUrl = i.getImgUrl();
            if (finalUrl != null && !finalUrl.startsWith("/api/restaurants/")) {
                finalUrl = "/api/restaurants" + (finalUrl.startsWith("/") ? "" : "/") + finalUrl;
            }

            img.setImgUrl(finalUrl);
            img.setThumbUrl(finalUrl);
            img.setCategory(i.getCategory() != null ? i.getCategory() : "GENERAL");
            img.setDisplayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0);
            img.setIsMain(i.getIsMain() != null ? i.getIsMain() : false);
            r.addImage(img);
        });
    }

    @Transactional
    public void deleteRestaurant(Integer id) {
        restaurantRepository.findByIdWithAllDetails(id)
                .orElseThrow()
                .setDeletedAt(LocalDateTime.now());
    }

    // ==========================================
    // --- [내부 헬퍼 메서드] ---
    // ==========================================

    private List<RestaurantDto.ImageResponseDto> mapImages(List<RestaurantImage> images) {
        if (images == null) return Collections.emptyList();

        // ✅ 정렬 추가
        List<RestaurantImage> validImages = images.stream()
                .filter(i -> i.getDeletedAt() == null)
                .sorted(Comparator.comparingInt(RestaurantImage::getDisplayOrder))
                .collect(Collectors.toList());

        return IntStream.range(0, validImages.size())
                .mapToObj(i -> {
                    RestaurantImage img = validImages.get(i);
                    return RestaurantDto.ImageResponseDto.builder()
                            .imgId(img.getImgId())
                            .imgUrl(img.getImgUrl())
                            .category(img.getCategory())
                            .isMain(i == 0)
                            .build();
                })
                .collect(Collectors.toList());
    }
}