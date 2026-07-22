package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminQueryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminSubAdminQueryRepositoryImpl
        implements AdminSubAdminQueryRepository {

    private final AdminSubAdminQueryJpaRepository jpaRepository;

    @Override
    public List<AdminSubAdminView> findAllByFestivalId(Long festivalId) {
        return jpaRepository.findAllByFestivalIdAndRoleAndStatus(
                festivalId,
                AdminRole.SUB_ADMIN,
                AdminStatus.ACTIVE
        );
    }

    @Override
    public Optional<AdminSubAdminView> findByFestivalIdAndPublicId(
            Long festivalId,
            UUID publicId
    ) {
        return jpaRepository.findByFestivalIdAndPublicIdAndRoleAndStatus(
                festivalId,
                publicId,
                AdminRole.SUB_ADMIN,
                AdminStatus.ACTIVE
        );
    }
}
