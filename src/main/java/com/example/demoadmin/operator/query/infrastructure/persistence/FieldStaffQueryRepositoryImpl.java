package com.example.demoadmin.operator.query.infrastructure.persistence;

import com.example.demoadmin.operator.command.domain.FieldStaffStatus;
import com.example.demoadmin.operator.query.application.dto.FieldStaffView;
import com.example.demoadmin.operator.query.repository.FieldStaffQueryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FieldStaffQueryRepositoryImpl implements FieldStaffQueryRepository {

    private final FieldStaffQueryJpaRepository jpaRepository;

    @Override
    public List<FieldStaffView> findAllByFestivalId(Long festivalId) {
        return jpaRepository.findAllByFestivalIdAndStatus(
                festivalId,
                FieldStaffStatus.ACTIVE
        );
    }

    @Override
    public Optional<FieldStaffView> findByFestivalIdAndPublicId(
            Long festivalId,
            UUID publicId
    ) {
        return jpaRepository.findByFestivalIdAndPublicIdAndStatus(
                festivalId,
                publicId,
                FieldStaffStatus.ACTIVE
        );
    }
}
