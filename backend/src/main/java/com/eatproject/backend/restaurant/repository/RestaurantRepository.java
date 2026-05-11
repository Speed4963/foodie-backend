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

    // 1. 키워드 검색 + 페이징 (직접 쿼리 작성 방식)
    // @Param("searchKeyword")를 쓰면 쿼리문의 :searchKeyword와 연결됩니다.
    @Query("SELECT r FROM Restaurant r " +
            "WHERE (:searchKeyword IS NULL OR r.name LIKE %:searchKeyword% OR r.address LIKE %:searchKeyword%) " +
            "AND r.deletedAt IS NULL")
    Page<Restaurant> selectRestaurantList(@Param("searchKeyword") String searchKeyword, Pageable pageable);

    // 2. 단일 상세 조회 (ID 기준)
    Optional<Restaurant> findByRestIdAndDeletedAtIsNull(Integer restId);

    /**
     * 3. 상세 페이지용 (성능 최적화: Fetch Join)
     */
    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "LEFT JOIN FETCH r.menus " +
            "LEFT JOIN FETCH r.images " +
            "WHERE r.restId = :restId AND r.deletedAt IS NULL")
    Optional<Restaurant> findByIdWithAllDetails(@Param("restId") Integer restId);

    // 4. 카테고리별 조회
    @Query("SELECT DISTINCT r FROM Restaurant r " +
            "JOIN r.tags t " +
            "WHERE t.category = :category " +
            "AND r.deletedAt IS NULL")
    Page<Restaurant> findAllByCategory(@Param("category") CategoryType category, Pageable pageable);

    List<Restaurant> findAllByGeohashInAndDeletedAtIsNull(List<String> geohashes);
}
