package com.example.demoadmin.dashboard.query.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FestivalDashboardView(
        UUID festivalId,
        String operatingStatus,
        long currentVisitorCount,
        long activeQueueCount,
        long averageWaitMinutes,
        LocalDateTime updatedAt
) {
}
