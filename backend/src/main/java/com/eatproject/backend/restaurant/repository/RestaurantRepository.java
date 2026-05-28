package com.eatproject.backend.restaurant.repository;

import com.eatproject.backend.common.CategoryType;
import com.eatproject.backend.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @Query(value = "SELECT DISTINCT r FROM Restaurant r " +
            "LEFT JOIN FETCH r.restaurantTag " +
            "LEFT JOIN FETCH r.images " +
            "WHERE (:searchKeyword IS NULL OR r.name LIKE %:searchKeyword% OR r.address LIKE %:searchKeyword%) " +
            "AND r.deletedAt IS NULL",
            // ✅ countQuery는 Fetch Join을 제거한 형태여야 페이징이 정확합니다.
            countQuery = "SELECT COUNT(r) FROM Restaurant r WHERE (:searchKeyword IS NULL OR r.name LIKE %:searchKeyword% OR r.address LIKE %:searchKeyword%) AND r.deletedAt IS NULL")
    Page<Restaurant> selectRestaurantList(@Param("searchKeyword") String searchKeyword, Pageable pageable);

    /**
     * 3. 상세 페이지용 (수정 버전)
     * ✅ [성능 최적화 반영]: 유저가 볼 상세 데이터와 관리용 수정을 위해
     * 메뉴(menus), 태그(restaurantTag)뿐만 아니라 이미지(images)까지 통째로 Fetch Join으로 당겨옵니다.
     */
    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "LEFT JOIN FETCH r.menus " +         // 메뉴 즉시 로딩
            "LEFT JOIN FETCH r.images " +        // ✅ 이미지 정보 즉시 로딩 추가!
            "LEFT JOIN FETCH r.restaurantTag " + // 태그 즉시 로딩
            "WHERE r.restId = :restId AND r.deletedAt IS NULL") // 삭제되지 않은 식당 검증 조건 추가 가능
    Optional<Restaurant> findByIdWithAllDetails(@Param("restId") Integer restId);

    // RestaurantRepository.java
    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "JOIN FETCH r.restaurantTag t " +
            "LEFT JOIN FETCH r.images " + // ✅ 사진 정보 한 번에 가져오기
            "LEFT JOIN FETCH r.menus " +  // ✅ 메뉴 정보 한 번에 가져오기
            "WHERE t.category = :category " +
            "AND r.deletedAt IS NULL")
    Page<Restaurant> findAllByCategory(@Param("category") CategoryType category, Pageable pageable);

    List<Restaurant> findAllByGeohashInAndDeletedAtIsNull(List<String> geohashes);
}