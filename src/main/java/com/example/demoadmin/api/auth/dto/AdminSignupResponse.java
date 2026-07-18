package com.example.demoadmin.api.auth.dto;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 관리자 회원가입 결과를 반환하는 HTTP 응답 DTO이다.
 */
@Schema(description = "관리자 회원가입 응답")
public record AdminSignupResponse(
        @Schema(description = "관리자 ID", example = "1")
        Long adminId,

        @Schema(description = "관리 대상 축제 ID. 축제 생성 전에는 null", example = "1")
        Long festivalId,

        @Schema(description = "로그인 이메일", example = "admin@mapo.go.kr")
        String email,

        @Schema(description = "관리자 이름", example = "홍길동")
        String name,

        @Schema(description = "소속 조직", example = "마포구청 소속")
        String organization,

        @Schema(description = "관리자 역할. 축제 생성 전에는 null", example = "FESTIVAL_OWNER")
        AdminRole role,

        @Schema(description = "계정 상태", example = "ACTIVE")
        AdminStatus status
) {

    /**
     * 관리자 계정 Aggregate에서 응답 DTO를 생성한다.
     */
    public static AdminSignupResponse from(AdminAccount adminAccount) {
        return new AdminSignupResponse(
                adminAccount.getId(),
                adminAccount.getFestivalId(),
                adminAccount.getEmailValue(),
                adminAccount.getNameValue(),
                adminAccount.getOrganizationValue(),
                adminAccount.getRole(),
                adminAccount.getStatus()
        );
    }
}

