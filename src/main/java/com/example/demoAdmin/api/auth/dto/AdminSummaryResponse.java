package com.example.demoadmin.api.auth.dto;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminRole;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 관리자 식별 정보와 역할별 권한 플래그를 제공하는 응답 DTO이다.
 */
@Schema(description = "관리자 요약 정보")
public record AdminSummaryResponse(
        @Schema(description = "관리자 ID", example = "1")
        Long adminId,

        @Schema(description = "관리 대상 축제 ID", example = "1")
        Long festivalId,

        @Schema(description = "로그인 이메일", example = "admin@mapo.go.kr")
        String email,

        @Schema(description = "관리자 이름", example = "홍길동")
        String name,

        @Schema(description = "소속 조직", example = "마포구청 소속")
        String organization,

        @Schema(description = "관리자 역할", example = "FESTIVAL_OWNER")
        AdminRole role,

        @Schema(description = "서브관리자 초대 가능 여부", example = "true")
        boolean canInviteSubAdmin,

        @Schema(description = "행사 정보 수정 가능 여부", example = "true")
        boolean canModifyFestivalInfo,

        @Schema(description = "줄 끝 갱신 가능 여부", example = "true")
        boolean canUpdateQueueTail
) {

    /**
     * 관리자 계정의 현재 역할과 권한 정보를 요약 응답으로 변환한다.
     */
    public static AdminSummaryResponse from(AdminAccount adminAccount) {
        return new AdminSummaryResponse(
                adminAccount.getId(),
                adminAccount.getFestivalId(),
                adminAccount.getEmailValue(),
                adminAccount.getNameValue(),
                adminAccount.getOrganizationValue(),
                adminAccount.getRole(),
                adminAccount.canInviteSubAdmin(),
                adminAccount.canModifyFestivalInfo(),
                adminAccount.canUpdateQueueTail()
        );
    }
}

