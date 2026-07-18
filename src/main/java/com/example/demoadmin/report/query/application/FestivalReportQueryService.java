package com.example.demoadmin.report.query.application;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import com.example.demoadmin.report.query.application.dto.FestivalReportSummaryView;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 축제 종료 후 결과 보고서 조회 유스케이스를 처리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalReportQueryService {

    private final AdminAccountRepository adminAccountRepository;

    /**
     * 담당 축제의 결과 보고서 요약 정보를 조회한다.
     */
    public FestivalReportSummaryView getSummary(
            Long festivalId,
            AdminPrincipal principal
    ) {
        validateReportAccess(festivalId, principal);

        // TODO(report): 실제 운영 DB/API 정보 확정 후 방문자, 혼잡 피크, 대기 시간 집계 결과로 교체한다.
        return new FestivalReportSummaryView(
                festivalId,
                0L,
                0L,
                0L,
                LocalDateTime.now()
        );
    }

    private void validateReportAccess(
            Long festivalId,
            AdminPrincipal principal
    ) {
        AdminAccount adminAccount = findAuthenticatedAdmin(principal);
        if (!festivalId.equals(adminAccount.getFestivalId())
                || !adminAccount.canViewOperationReport()) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private AdminAccount findAuthenticatedAdmin(AdminPrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return adminAccountRepository.findById(principal.adminId())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }
}
