package com.example.demoadmin.report.query.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.report.query.application.dto.FestivalReportSummaryView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FestivalReportQueryServiceIntegrationTest {

    @Autowired
    private FestivalReportQueryService reportQueryService;

    @Autowired
    private AdminAccountRepository adminAccountRepository;

    @Nested
    @DisplayName("getSummary")
    class GetSummary {

        @Test
        @DisplayName("담당 축제 결과 보고서 요약을 조회한다")
        void success_GetSummary_FestivalOwner() {
            // given
            Long festivalId = 1L;
            AdminAccount adminAccount = adminAccountRepository.save(
                    festivalOwner(festivalId)
            );

            // when
            FestivalReportSummaryView view = reportQueryService.getSummary(
                    festivalId,
                    principal(adminAccount)
            );

            // then
            assertThat(view.festivalId()).isEqualTo(festivalId);
            assertThat(view.totalVisitorCount()).isZero();
        }
    }

    private AdminPrincipal principal(AdminAccount adminAccount) {
        return new AdminPrincipal(
                adminAccount.getId(),
                adminAccount.getFestivalId(),
                adminAccount.getEmailValue(),
                adminAccount.getRole()
        );
    }

    private AdminAccount festivalOwner(Long festivalId) {
        return AdminAccount.createFestivalOwner(
                AdminEmail.of("owner@mapo.go.kr"),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                festivalId,
                AdminPasswordHash.of("encoded-password")
        );
    }
}
