package com.example.demoadmin.operator.command.infrastructure.persistence;

import com.example.demoadmin.operator.command.domain.FieldStaffAccount;
import com.example.demoadmin.operator.command.domain.vo.FieldStaffLoginId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface FieldStaffAccountJpaRepository extends JpaRepository<FieldStaffAccount, Long> {

    Optional<FieldStaffAccount> findByPublicId(UUID publicId);

    Optional<FieldStaffAccount> findByFestivalIdAndLoginId(
            Long festivalId,
            FieldStaffLoginId loginId
    );

    boolean existsByFestivalIdAndLoginId(
            Long festivalId,
            FieldStaffLoginId loginId
    );
}
