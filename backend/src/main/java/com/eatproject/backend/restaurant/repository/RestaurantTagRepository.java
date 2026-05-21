package com.eatproject.backend.restaurant.repository;

import com.eatproject.backend.restaurant.entity.RestaurantTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTagRepository extends JpaRepository<RestaurantTag, Integer> {
    // 나중에 카테고리별 통계나 특정 카테고리명 검색이 필요하면 여기에 쿼리를 추가하면 됩니다!
}