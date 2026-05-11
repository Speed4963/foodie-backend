package com.eatproject.backend.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigDto {

    @NotBlank(message = "사이트 이름은 필수입니다.")
    private String siteName;

    private String footerInfo;

    private Boolean maintenanceMode;

    @Min(value = 1, message = "알림 임계값은 1 이상이어야 합니다.")
    private Integer alertThreshold;

    @Min(value = 10, message = "스레드 제한은 최소 10개 이상이어야 합니다.")
    private Integer threadReplyLimit;

    @Min(value = 10, message = "게시판 제한은 최소 10개 이상이어야 합니다.")
    private Integer boardThreadLimit;
}