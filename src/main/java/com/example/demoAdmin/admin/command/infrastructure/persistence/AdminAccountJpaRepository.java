package com.example.demoadmin.admin.command.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface AdminAccountJpaRepository extends JpaRepository<AdminAccount, Long> {

    boolean existsByEmail(AdminEmail email);

    boolean existsByFestivalIdAndRole(Long festivalId, AdminRole role);

    Optional<AdminAccount> findByEmail(AdminEmail email);
}
