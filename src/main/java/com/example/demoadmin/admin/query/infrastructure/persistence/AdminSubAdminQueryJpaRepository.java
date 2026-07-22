package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface AdminSubAdminQueryJpaRepository
        extends JpaRepository<AdminAccount, Long> {

    List<AdminAccount> findAllByFestivalIdAndRoleAndStatusOrderByIdAsc(
            Long festivalId,
            AdminRole role,
            AdminStatus status
    );

    Optional<AdminAccount> findByFestivalIdAndPublicIdAndRoleAndStatus(
            Long festivalId,
            UUID publicId,
            AdminRole role,
            AdminStatus status
    );
}
