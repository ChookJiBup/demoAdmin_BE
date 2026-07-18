package com.example.demoadmin.report.query.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import com.example.demoadmin.report.query.application.dto.FestivalReportSummaryView;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FestivalReportQueryServiceTest {

    @InjectMocks
    private FestivalReportQueryService reportQueryService;

    @Mock
    private AdminAccountRepository adminAccountRepository;

    @Nested
    @DisplayName("getSummary")
    class GetSummary {

        @Test
        @DisplayName("담당 축제의 결과 보고서 요약을 조회한다")
        void success_GetSummary_FestivalOwner() {
            // given
            Long festivalId = 1L;
            AdminPrincipal principal = principal(festivalId, AdminRole.FESTIVAL_OWNER);
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(festivalOwner(festivalId)));

            // when
            FestivalReportSummaryView view = reportQueryService.getSummary(
                    festivalId,
                    principal
            );

            // then
            assertThat(view.festivalId()).isEqualTo(festivalId);
            assertThat(view.totalVisitorCount()).isZero();
            assertThat(view.peakConcurrentVisitorCount()).isZero();
        }

        @Test
        @DisplayName("다른 축제의 결과 보고서 요약은 조회할 수 없다")
        void fail_GetSummary_DifferentFestival_CustomException() {
            // given
            Long festivalId = 1L;
            AdminPrincipal principal = principal(2L, AdminRole.FESTIVAL_OWNER);
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(festivalOwner(2L)));

            // when & then
            assertThatThrownBy(() -> reportQueryService.getSummary(
                    festivalId,
                    principal
            ))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.FORBIDDEN.getMessage());
        }

        @Test
        @DisplayName("축제에 배정되지 않은 관리자는 결과 보고서 요약을 조회할 수 없다")
        void fail_GetSummary_UnassignedAdmin_CustomException() {
            // given
            Long festivalId = 1L;
            AdminPrincipal principal = principal(null, null);
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(unassignedAdmin()));

            // when & then
            assertThatThrownBy(() -> reportQueryService.getSummary(
                    festivalId,
                    principal
            ))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.FORBIDDEN.getMessage());
        }
    }

    private AdminPrincipal principal(
            Long festivalId,
            AdminRole role
    ) {
        return new AdminPrincipal(
                1L,
                festivalId,
                "owner@mapo.go.kr",
                role
        );
    }

    private AdminAccount unassignedAdmin() {
        return AdminAccount.createAdmin(
                AdminEmail.of("owner@mapo.go.kr"),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                AdminPasswordHash.of("encoded-password")
        );
    }

    private AdminAccount festivalOwner(Long festivalId) {
        AdminAccount adminAccount = unassignedAdmin();
        adminAccount.assignFestivalOwner(festivalId);
        return adminAccount;
    }
}
