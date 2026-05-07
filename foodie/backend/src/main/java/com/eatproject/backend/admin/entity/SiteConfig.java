package com.eatproject.backend.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SITE_CONFIGS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SiteConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ID")
    private Integer configId;

    @Column(name = "SITE_NAME", length = 50, nullable = false)
    private String siteName;

    @Column(name = "FOOTER_INFO", nullable = false, columnDefinition = "TEXT")
    private String footerInfo;

    @Builder.Default
    @Column(name = "MAINTENANCE_MODE", nullable = false)
    private Boolean maintenanceMode = false;

    @Column(name = "ALERT_THRESHOLD", nullable = false)
    private Integer alertThreshold;

    @Builder.Default
    @Column(name = "THREAD_REPLY_LIMIT", nullable = false)
    private Integer threadReplyLimit = 500;

    @Builder.Default
    @Column(name = "BOARD_THREAD_LIMIT", nullable = false)
    private Integer boardThreadLimit = 1000;

    @Column(name = "UPDATED_BY", nullable = false)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}