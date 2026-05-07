package com.eatproject.backend.admin.repository;

import com.eatproject.backend.admin.entity.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteConfigRepository extends JpaRepository<SiteConfig, Integer> {
}
