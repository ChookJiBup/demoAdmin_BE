package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminQueryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminSubAdminQueryRepositoryImpl
        implements AdminSubAdminQueryRepository {

    private static final String SUB_ADMIN_SELECT = """
            select new com.example.demoadmin.admin.query.application.dto.AdminSubAdminView(
                a.publicId,
                a.email.value,
                a.name.value,
                a.organization.value,
                a.status
            )
            from AdminAccount a
            """;

    private final EntityManager entityManager;

    @Override
    public List<AdminSubAdminView> searchInvitedSubAdmins(
            Long festivalId,
            Long invitedByAdminId,
            String keyword
    ) {
        return entityManager.createQuery(
                        SUB_ADMIN_SELECT + """
                                where a.festivalId = :festivalId
                                  and a.invitedByAdminId = :invitedByAdminId
                                  and a.role = :role
                                  and a.status = :status
                                  and (
                                      :keyword is null
                                      or lower(a.email.value) like concat('%', :keyword, '%')
                                      or lower(a.name.value) like concat('%', :keyword, '%')
                                      or lower(a.organization.value) like concat('%', :keyword, '%')
                                  )
                                order by a.id asc
                                """,
                        AdminSubAdminView.class
                )
                .setParameter("festivalId", festivalId)
                .setParameter("invitedByAdminId", invitedByAdminId)
                .setParameter("role", AdminRole.SUB_ADMIN)
                .setParameter("status", AdminStatus.ACTIVE)
                .setParameter("keyword", keyword)
                .getResultList();
    }

    @Override
    public Optional<AdminSubAdminView> findInvitedSubAdmin(
            Long festivalId,
            Long invitedByAdminId,
            UUID publicId
    ) {
        return entityManager.createQuery(
                        SUB_ADMIN_SELECT + """
                                where a.festivalId = :festivalId
                                  and a.invitedByAdminId = :invitedByAdminId
                                  and a.publicId = :publicId
                                  and a.role = :role
                                  and a.status = :status
                                """,
                        AdminSubAdminView.class
                )
                .setParameter("festivalId", festivalId)
                .setParameter("invitedByAdminId", invitedByAdminId)
                .setParameter("publicId", publicId)
                .setParameter("role", AdminRole.SUB_ADMIN)
                .setParameter("status", AdminStatus.ACTIVE)
                .getResultStream()
                .findFirst();
    }
}
