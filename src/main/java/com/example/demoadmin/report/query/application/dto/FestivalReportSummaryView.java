package com.example.demoadmin.report.query.application.dto;

import java.time.LocalDateTime;

public record FestivalReportSummaryView(
        Long festivalId,
        long totalVisitorCount,
        long peakConcurrentVisitorCount,
        long averageWaitMinutes,
        LocalDateTime generatedAt
) {
}
