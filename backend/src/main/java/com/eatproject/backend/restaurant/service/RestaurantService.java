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
        // Repository에서 엔티티 페이지를 가져온 후 DTO로 변환(map)합니다.
        return restaurantRepository.selectRestaurantList(searchKeyword, pageable).map(e -> {
            RestaurantDto dto = new RestaurantDto();
            dto.setRestId(e.getRestId());
            dto.setName(e.getName());
            dto.setAddress(e.getAddress());
            dto.setAvgPrice(e.getAvgPrice());
            dto.setPhone(e.getPhone());
            dto.setBusinessHours(e.getBusinessHours());
            dto.setClosedDays(e.getClosedDays());

            // 카테고리 태그 설정
            if (e.getRestaurantTag() != null) {
                dto.setCategory(e.getRestaurantTag().getCategory().name());
            }

            // ✅ 이미지 리스트 변환 및 세팅
            if (e.getImages() != null) {
                // 소프트 딜리트(삭제 처리)되지 않은 유효한 이미지만 필터링합니다.
                List<RestaurantImage> validImages = e.getImages().stream()
                        .filter(i -> i.getDeletedAt() == null)
                        .collect(Collectors.toList());

                // IntStream을 사용하여 인덱스 기반으로 0번째 이미지를 메인(isMain = true)으로 설정합니다.
                dto.setImages(IntStream.range(0, validImages.size())
                        .mapToObj(i -> {
                            RestaurantImage img = validImages.get(i);
                            return RestaurantDto.ImageResponseDto.builder()
                                    .imgId(img.getImgId())
                                    .imgUrl(springip + img.getImgUrl()) // 프론트에서 접근 가능하도록 전체 경로 조립
                                    .category(img.getCategory())
                                    .isMain(i == 0) // 첫 번째 사진만 true
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
        // 카테고리 값이 없거나 "ALL"인 경우 전체 조회 메서드로 넘깁니다.
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            return selectRestaurantList(null, pageable);
        }

        try {
            // 문자열을 Enum 타입으로 안전하게 변환합니다.
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

                // 이미지 리스트 변환 로직 (selectRestaurantList와 동일하게 유효 이미지 필터링 및 전체 경로 매핑)
                if (e.getImages() != null) {
                    List<RestaurantImage> validImages = e.getImages().stream()
                            .filter(i -> i.getDeletedAt() == null)
                            .collect(Collectors.toList());

                    dto.setImages(IntStream.range(0, validImages.size())
                            .mapToObj(i -> {
                                RestaurantImage img = validImages.get(i);
                                return RestaurantDto.ImageResponseDto.builder()
                                        .imgId(img.getImgId())
                                        .imgUrl(springip + img.getImgUrl()) // 리액트 주소 예: "http://localhost:8080"
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
            // 일치하는 카테고리 Enum이 없으면 빈 페이지를 반환하여 에러를 방지합니다.
            return Page.empty(pageable);
        }
    }

    /**
     * 식당 상세 정보 단건 조회
     */
    public RestaurantDto findById(Integer id) {
        // Fetch Join 등을 통해 연관 데이터(메뉴, 이미지)를 한 번에 가져와 N+1 문제를 방지합니다.
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
            dto.setCustomTag("#" + e.getRestaurantTag().getCustomTag()); // 해시태그 형식으로 래핑
        }

        // 메뉴 목록 매핑: 삭제되지 않은 메뉴만 필터링하여 DTO로 변환
        dto.setMenus(e.getMenus().stream()
                .filter(m -> m.getDeletedAt() == null)
                .map(m -> RestaurantDto.MenuResponseDto.builder()
                        .menuId(m.getMenuId())
                        .pName(m.getPName())
                        .price(m.getPrice())
                        .isRepresentative(m.getIsRepresentative())
                        .build())
                .collect(Collectors.toList()));

        // 이미지 매핑 로직 (목록 조회와 동일)
        if (e.getImages() != null) {
            List<RestaurantImage> validImages = e.getImages().stream()
                    .filter(i -> i.getDeletedAt() == null)
                    .collect(Collectors.toList());

            dto.setImages(IntStream.range(0, validImages.size())
                    .mapToObj(i -> {
                        RestaurantImage img = validImages.get(i);
                        return RestaurantDto.ImageResponseDto.builder()
                                .imgId(img.getImgId())
                                .imgUrl(springip + img.getImgUrl()) // DB 경로에 서버 IP를 붙여 완성된 URL 제공
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

    /**
     * 신규 식당 등록
     * DB 쓰기 작업이므로 @Transactional을 명시하여 데이터 무결성을 보장합니다.
     */
    @Transactional
    public Integer saveRestaurant(RestaurantCreateDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());

        // 네이버 지도 API를 활용하여 텍스트 주소를 위도/경도 좌표로 변환합니다.
        NaverMapService.Coordinate coord = (dto.getAddress() != null && !dto.getAddress().isBlank())
                ? naverMapService.getCoordinate(dto.getAddress())
                : null;

        // API에서 좌표를 받아왔으면 해당 값을, 실패했거나 없으면 DTO 값 또는 0을 기본값으로 세팅합니다.
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

        // ❗ 복구 완료: 외래 키(Tag) 연관 관계 매핑
        if (dto.getTagId() != null) {
            restaurant.setRestaurantTag(restaurantTagRepository.findById(dto.getTagId()).orElseThrow());
        }

        // ❗ 복구 완료: 메뉴(Menu) 저장 로직
        if (dto.getMenus() != null) dto.getMenus().forEach(m -> {
            Menu menu = new Menu();
            menu.setPName(m.getPName());
            menu.setPrice(m.getPrice());
            menu.setIsRepresentative(m.getIsRepresentative() != null ? m.getIsRepresentative() : false);
            restaurant.addMenu(menu);
        });

        // 이미지 저장 로직 (초간단 URL 자동 생성 포함)
        if (dto.getImages() != null) dto.getImages().forEach(i -> {
            RestaurantImage img = new RestaurantImage();

            // /api/restaurants/ 로 시작하지 않으면 강제로 무조건 붙임
            String finalUrl = i.getImgUrl();
            if (finalUrl != null && !finalUrl.startsWith("/api/restaurants/")) {
                finalUrl = "/api/restaurants" + (finalUrl.startsWith("/") ? "" : "/") + finalUrl;
            }

            img.setImgUrl(finalUrl);
            img.setThumbUrl(finalUrl);
            img.setCategory(i.getCategory() != null ? i.getCategory() : "GENERAL");
            img.setDisplayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0);
            img.setIsMain(i.getIsMain() != null ? i.getIsMain() : false);

            // ❗ 복구 완료: 주석이 아니라 실제로 추가되도록 수정
            restaurant.addImage(img);
        });

        // ❗ 복구 완료: 영속성 컨텍스트에 저장 후 자동 생성된 PK 반환
        return restaurantRepository.save(restaurant).getRestId();
    }

    /**
     * 카테고리(태그) 정보 업데이트
     */
    @Transactional // 누락되었을 경우를 대비해 쓰기 작업에는 트랜잭션 보장이 필요합니다. (원래 코드엔 없었으나 권장됨)
    public void updateCategoryInfo(Integer tagId, String newCustomTag) {
        // 1. 태그를 찾고, 없으면 예외 발생
        RestaurantTag tag = restaurantTagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("해당 카테고리 태그가 존재하지 않습니다."));

        // 2. 새로운 태그 이름으로 업데이트 (해시태그 샵(#) 기호와 앞뒤 공백 제거 처리)
        tag.setCustomTag(newCustomTag.replace("#", "").trim());
    }

    /**
     * 기존 식당 정보 수정
     */
    @Transactional
    public void updateRestaurant(Integer id, RestaurantUpdateDto dto) {
        // 더티 체킹(Dirty Checking)을 위해 기존 엔티티를 조회합니다.
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

        // 하위 엔티티(메뉴) 컬렉션을 비우고 새로 채워 넣습니다.
        // JPA의 orphanRemoval = true 설정이 되어 있다면 기존 데이터는 DB에서 삭제(또는 고아 객체 처리)됩니다.
        r.getMenus().clear();
        if (dto.getMenus() != null) dto.getMenus().forEach(m -> {
            Menu menu = new Menu();
            menu.setPName(m.getPName());
            menu.setPrice(m.getPrice());
            menu.setIsRepresentative(m.getIsRepresentative() != null ? m.getIsRepresentative() : false);
            r.addMenu(menu);
        });

        // 하위 엔티티(이미지) 컬렉션을 비우고 새로 채워 넣습니다.
        r.getImages().clear();
        if (dto.getImages() != null) dto.getImages().forEach(i -> {
            RestaurantImage img = new RestaurantImage();

            // ✅ URL 초간단 조립 로직: /api/restaurants/ 가 없으면 무조건 붙여줌
            String finalUrl = i.getImgUrl();
            if (finalUrl != null && !finalUrl.startsWith("/api/restaurants/")) {
                finalUrl = "/api/restaurants" + (finalUrl.startsWith("/") ? "" : "/") + finalUrl;
            }

            img.setImgUrl(finalUrl);
            img.setThumbUrl(finalUrl); // 썸네일도 동일한 경로 적용
            img.setCategory(i.getCategory() != null ? i.getCategory() : "GENERAL");
            img.setDisplayOrder(i.getDisplayOrder() != null ? i.getDisplayOrder() : 0);
            img.setIsMain(i.getIsMain() != null ? i.getIsMain() : false);
            r.addImage(img);
        });
    }

    /**
     * 식당 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteRestaurant(Integer id) {
        // 실제 DELETE 쿼리를 날리지 않고, 삭제 시간(DeletedAt)을 기록하여 논리적으로만 삭제 처리합니다.
        restaurantRepository.findByIdWithAllDetails(id)
                .orElseThrow()
                .setDeletedAt(LocalDateTime.now());
    }

    // ==========================================
    // --- [내부 헬퍼 메서드] ---
    // ==========================================

    /**
     * 이미지 엔티티 리스트를 DTO 리스트로 변환하는 유틸리티 메서드
     * (현재 내부 조회 로직에서 직접 처리하고 있으나, 코드 재사용을 위해 만들어진 것으로 보입니다)
     */
    private List<RestaurantDto.ImageResponseDto> mapImages(List<RestaurantImage> images) {
        if (images == null) return Collections.emptyList();

        // 1. 삭제되지 않은 유효한 이미지들만 필터링
        List<RestaurantImage> validImages = images.stream()
                .filter(i -> i.getDeletedAt() == null)
                .collect(Collectors.toList());

        // 2. 인덱스(i)를 활용해 0번째 사진만 대표(isMain = true)로 설정 후 DTO 변환
        return IntStream.range(0, validImages.size())
                .mapToObj(i -> {
                    RestaurantImage img = validImages.get(i);
                    return RestaurantDto.ImageResponseDto.builder()
                            .imgId(img.getImgId())
                            .imgUrl(img.getImgUrl()) // 이 메서드에서는 springip가 붙어있지 않음에 주의
                            .category(img.getCategory())
                            .isMain(i == 0) // ✅ 0번째만 true, 나머지는 false
                            .build();
                })
                .collect(Collectors.toList());
    }
}