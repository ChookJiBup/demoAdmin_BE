package com.example.demoadmin.operator.query.application.dto;

import com.example.demoadmin.operator.command.domain.FieldStaffStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 현장 스태프 계정 조회 결과를 표현한다.
 */
public record FieldStaffView(
        UUID staffId,
        String loginId,
        String name,
        String phoneNumber,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        FieldStaffStatus status
) {
}
