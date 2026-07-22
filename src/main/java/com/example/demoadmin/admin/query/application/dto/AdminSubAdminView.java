package com.example.demoadmin.admin.query.application.dto;

import com.example.demoadmin.admin.command.domain.AdminStatus;
import java.util.UUID;

/**
 * 서브관리자 조회 결과를 표현한다.
 */
public record AdminSubAdminView(
        UUID adminId,
        String email,
        String name,
        String organization,
        AdminStatus status
) {
}
