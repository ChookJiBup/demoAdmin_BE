package com.example.demoadmin.auth.support;

import com.example.demoadmin.admin.command.domain.AdminRole;

/**
 * JWT 인증 후 SecurityContext에 저장되는 관리자 인증 주체이다.
 */
public record AdminPrincipal(
        Long adminId,
        Long festivalId,
        String email,
        AdminRole role
) {
}
