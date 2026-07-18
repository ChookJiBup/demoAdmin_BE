package com.example.demoadmin.dashboard.query.application.dto;

import java.time.LocalDateTime;

public record FestivalDashboardView(
        Long festivalId,
        String operatingStatus,
        long currentVisitorCount,
        long activeQueueCount,
        long averageWaitMinutes,
        LocalDateTime updatedAt
) {
}
