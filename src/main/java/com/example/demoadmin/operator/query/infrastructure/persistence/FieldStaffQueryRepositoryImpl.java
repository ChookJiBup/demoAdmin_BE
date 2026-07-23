package com.example.demoadmin.operator.query.infrastructure.persistence;

import com.example.demoadmin.operator.command.domain.FieldStaffStatus;
import com.example.demoadmin.operator.query.application.dto.FieldStaffView;
import com.example.demoadmin.operator.query.repository.FieldStaffQueryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FieldStaffQueryRepositoryImpl implements FieldStaffQueryRepository {

    private static final String FIELD_STAFF_SELECT = """
            select new com.example.demoadmin.operator.query.application.dto.FieldStaffView(
                f.publicId,
                f.loginId.value,
                f.name.value,
                f.phoneNumber.value,
                f.validFrom,
                f.validUntil,
                f.status
            )
            from FieldStaffAccount f
            """;

    private final EntityManager entityManager;

    @Override
    public List<FieldStaffView> findAllByFestivalId(Long festivalId) {
        return entityManager.createQuery(
                        FIELD_STAFF_SELECT + """
                                where f.festivalId = :festivalId
                                  and f.status = :status
                                order by f.id asc
                                """,
                        FieldStaffView.class
                )
                .setParameter("festivalId", festivalId)
                .setParameter("status", FieldStaffStatus.ACTIVE)
                .getResultList();
    }

    @Override
    public Optional<FieldStaffView> findByFestivalIdAndPublicId(
            Long festivalId,
            UUID publicId
    ) {
        return entityManager.createQuery(
                        FIELD_STAFF_SELECT + """
                                where f.festivalId = :festivalId
                                  and f.publicId = :publicId
                                  and f.status = :status
                                """,
                        FieldStaffView.class
                )
                .setParameter("festivalId", festivalId)
                .setParameter("publicId", publicId)
                .setParameter("status", FieldStaffStatus.ACTIVE)
                .getResultStream()
                .findFirst();
    }
}
