package com.example.demoadmin.admin.command.domain;

import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import java.util.Optional;

/**
 * 축제 관리자 계정을 조회하고 저장하는 저장소 계약이다.
 */
public interface AdminAccountRepository {

    /**
     * 관리자 계정을 저장한다.
     */
    AdminAccount save(AdminAccount adminAccount);

    /**
     * 관리자 식별자로 계정을 조회한다.
     */
    Optional<AdminAccount> findById(Long adminAccountId);

    /**
     * 같은 이메일로 가입된 관리자 계정이 있는지 확인한다.
     */
    boolean existsByEmail(AdminEmail email);

    /**
     * 지정한 축제에 특정 관리자 역할이 이미 존재하는지 확인한다.
     */
    boolean existsByFestivalIdAndRole(Long festivalId, AdminRole role);

    /**
     * 로그인 이메일로 관리자 계정을 조회한다.
     */
    Optional<AdminAccount> findByEmail(AdminEmail email);
}
