package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminManagedFestivalCondition;
import com.example.demoadmin.admin.query.application.dto.AdminManagedFestivalView;
import com.example.demoadmin.admin.query.repository.AdminManagedFestivalQueryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminManagedFestivalQueryRepositoryImpl
        implements AdminManagedFestivalQueryRepository {

    private static final String MANAGED_FESTIVAL_SELECT = """
            select new com.example.demoadmin.admin.query.application.dto.AdminManagedFestivalView(
                f.publicId,
                f.name.value,
                f.year,
                a.role,
                f.status,
                f.address.value,
                f.period.startDate,
                f.period.endDate
            )
            from AdminAccount a
            join Festival f on f.id = a.festivalId
            """;

    private final EntityManager entityManager;

    @Override
    public List<AdminManagedFestivalView> searchCurrentManagedFestivals(
            Long adminAccountId,
            AdminManagedFestivalCondition condition
    ) {
        return entityManager.createQuery(
                        MANAGED_FESTIVAL_SELECT + """
                                where a.id = :adminAccountId
                                  and a.status = :adminStatus
                                  and a.festivalId is not null
                                  and a.role is not null
                                  and (:role is null or a.role = :role)
                                  and (:year is null or f.year = :year)
                                  and (
                                      :keyword is null
                                      or lower(f.name.value) like concat('%', :keyword, '%')
                                      or lower(f.address.value) like concat('%', :keyword, '%')
                                  )
                                order by f.year desc, f.id desc
                                """,
                        AdminManagedFestivalView.class
                )
                .setParameter("adminAccountId", adminAccountId)
                .setParameter("adminStatus", AdminStatus.ACTIVE)
                .setParameter("role", condition.role())
                .setParameter("year", condition.year())
                .setParameter("keyword", condition.keyword())
                .getResultList();
    }

    @Override
    public Optional<AdminManagedFestivalView> findCurrentManagedFestival(
            Long adminAccountId,
            UUID festivalId
    ) {
        return entityManager.createQuery(
                        MANAGED_FESTIVAL_SELECT + """
                                where a.id = :adminAccountId
                                  and a.status = :adminStatus
                                  and a.festivalId is not null
                                  and a.role is not null
                                  and f.publicId = :festivalId
                                """,
                        AdminManagedFestivalView.class
                )
                .setParameter("adminAccountId", adminAccountId)
                .setParameter("adminStatus", AdminStatus.ACTIVE)
                .setParameter("festivalId", festivalId)
                .getResultStream()
                .findFirst();
    }
}
