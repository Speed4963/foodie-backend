package com.eatproject.backend.admin.service;

import com.eatproject.backend.admin.dto.AdminLogResponseDto;
import com.eatproject.backend.admin.dto.SiteConfigDto;
import com.eatproject.backend.admin.entity.AdminLog;
import com.eatproject.backend.admin.entity.SiteConfig;
import com.eatproject.backend.admin.repository.AdminLogRepository;
import com.eatproject.backend.admin.repository.SiteConfigRepository;
import com.eatproject.backend.board.entity.Board;
import com.eatproject.backend.board.repository.BoardRepository;
import com.eatproject.backend.member.entity.Member;
import com.eatproject.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminLogRepository adminLogRepository;
    private final SiteConfigRepository siteConfigRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void banUser(String adminEmail, String targetEmail, String reason) {
        Member member = memberRepository.findById(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        member.setIsBanned(true);

        adminLogRepository.save(AdminLog.builder()
                .adminEmail(adminEmail).actionType("BAN_USER")
                .bannedUser(targetEmail).reason(reason).build());
    }

    @Transactional
    public void approveBoard(String adminEmail, Integer boardId, String reason) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다."));
        board.updateStatus("ACTIVE");

        adminLogRepository.save(AdminLog.builder()
                .adminEmail(adminEmail).actionType("APPROVE_BOARD")
                .approvedBoard(boardId).reason(reason).build());
    }

    @Transactional
    public void updateSiteConfig(String adminEmail, SiteConfigDto dto) {
        SiteConfig config = siteConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalStateException("설정 데이터가 없습니다."));

        config.setSiteName(dto.getSiteName());
        config.setFooterInfo(dto.getFooterInfo());
        config.setMaintenanceMode(dto.getMaintenanceMode());
        config.setAlertThreshold(dto.getAlertThreshold());
        config.setThreadReplyLimit(dto.getThreadReplyLimit());
        config.setBoardThreadLimit(dto.getBoardThreadLimit());
        config.setUpdatedBy(adminEmail);
    }

    public List<AdminLogResponseDto> getAllLogs() {
        return adminLogRepository.findAll().stream()
                .map(log -> AdminLogResponseDto.builder()
                        .logId(log.getLogId()).adminEmail(log.getAdminEmail())
                        .actionType(log.getActionType()).reason(log.getReason())
                        .createdAt(log.getCreatedAt()).build())
                .toList();
    }
}