package com.example.demoadmin.operator.query.infrastructure.persistence;

import com.example.demoadmin.operator.command.domain.QFieldStaffAccount;
import com.example.demoadmin.operator.command.domain.FieldStaffStatus;
import com.example.demoadmin.operator.query.application.dto.FieldStaffView;
import com.example.demoadmin.operator.query.repository.FieldStaffQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FieldStaffQueryRepositoryImpl implements FieldStaffQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FieldStaffView> findAllByFestivalId(Long festivalId) {
        QFieldStaffAccount fieldStaffAccount =
                QFieldStaffAccount.fieldStaffAccount;

        return queryFactory
                .select(Projections.constructor(
                        FieldStaffView.class,
                        fieldStaffAccount.publicId,
                        fieldStaffAccount.loginId.value,
                        fieldStaffAccount.name.value,
                        fieldStaffAccount.phoneNumber.value,
                        fieldStaffAccount.validFrom,
                        fieldStaffAccount.validUntil,
                        fieldStaffAccount.status
                ))
                .from(fieldStaffAccount)
                .where(
                        fieldStaffAccount.festivalId.eq(festivalId),
                        fieldStaffAccount.status.eq(FieldStaffStatus.ACTIVE)
                )
                .orderBy(fieldStaffAccount.id.asc())
                .fetch();
    }

    @Override
    public Optional<FieldStaffView> findByFestivalIdAndPublicId(
            Long festivalId,
            UUID publicId
    ) {
        QFieldStaffAccount fieldStaffAccount =
                QFieldStaffAccount.fieldStaffAccount;

        FieldStaffView result = queryFactory
                .select(Projections.constructor(
                        FieldStaffView.class,
                        fieldStaffAccount.publicId,
                        fieldStaffAccount.loginId.value,
                        fieldStaffAccount.name.value,
                        fieldStaffAccount.phoneNumber.value,
                        fieldStaffAccount.validFrom,
                        fieldStaffAccount.validUntil,
                        fieldStaffAccount.status
                ))
                .from(fieldStaffAccount)
                .where(
                        fieldStaffAccount.festivalId.eq(festivalId),
                        fieldStaffAccount.publicId.eq(publicId),
                        fieldStaffAccount.status.eq(FieldStaffStatus.ACTIVE)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
