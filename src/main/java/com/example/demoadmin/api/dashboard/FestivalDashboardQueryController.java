package com.example.demoadmin.api.dashboard;

import com.example.demoadmin.api.dashboard.dto.FestivalDashboardResponse;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.dashboard.query.application.FestivalDashboardQueryService;
import com.example.demoadmin.global.response.ApiResponse;
import com.example.demoadmin.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 축제 진행 중 대시보드 조회 API를 제공한다.
 */
@Tag(name = "Festival Dashboard", description = "축제 진행 중 대시보드 API")
@RestController
@RequestMapping("/api/festivals/{festivalId}/dashboard")
@RequiredArgsConstructor
public class FestivalDashboardQueryController {

    private final FestivalDashboardQueryService dashboardQueryService;

    /**
     * 담당 축제의 진행 중 대시보드 요약 정보를 조회한다.
     */
    @Operation(summary = "축제 진행 중 대시보드 조회")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ApiResponse<FestivalDashboardResponse> getDashboard(
            @PathVariable UUID festivalId,
            @AuthenticationPrincipal AdminPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.FESTIVAL_DASHBOARD_READ_SUCCESS,
                FestivalDashboardResponse.from(
                        dashboardQueryService.getDashboard(festivalId, principal)
                )
        );
    }
}
