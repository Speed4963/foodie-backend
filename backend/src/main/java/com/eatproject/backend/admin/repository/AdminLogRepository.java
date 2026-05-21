package com.eatproject.backend.admin.repository;

import com.eatproject.backend.admin.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}

