package com.example.demoadmin.dashboard.query.application;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.dashboard.query.application.dto.FestivalDashboardView;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 축제 진행 중 대시보드 조회 유스케이스를 처리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FestivalDashboardQueryService {

    private final AdminAccountRepository adminAccountRepository;

    /**
     * 담당 축제의 진행 중 대시보드 요약 정보를 조회한다.
     */
    public FestivalDashboardView getDashboard(
            Long festivalId,
            AdminPrincipal principal
    ) {
        validateReportAccess(festivalId, principal);

        // TODO(dashboard): 실제 운영 DB/API 정보 확정 후 실시간 혼잡도, 대기열, 방문자 지표 조회로 교체한다.
        return new FestivalDashboardView(
                festivalId,
                "PREPARING",
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
