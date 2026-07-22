package com.example.demoadmin.admin.query.application.dto;

import com.example.demoadmin.admin.command.domain.AdminAccount;
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

    /**
     * 관리자 계정 Aggregate를 서브관리자 조회 결과로 변환한다.
     */
    public static AdminSubAdminView from(AdminAccount adminAccount) {
        return new AdminSubAdminView(
                adminAccount.getPublicId(),
                adminAccount.getEmailValue(),
                adminAccount.getNameValue(),
                adminAccount.getOrganizationValue(),
                adminAccount.getStatus()
        );
    }
}
