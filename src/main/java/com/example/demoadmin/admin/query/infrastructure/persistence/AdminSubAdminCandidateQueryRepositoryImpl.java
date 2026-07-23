package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminCandidateView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminCandidateQueryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminSubAdminCandidateQueryRepositoryImpl
        implements AdminSubAdminCandidateQueryRepository {

    private final EntityManager entityManager;

    @Override
    public List<AdminSubAdminCandidateView> searchCandidates(String keyword) {
        return entityManager.createQuery(
                        """
                                select new com.example.demoadmin.admin.query.application.dto.AdminSubAdminCandidateView(
                                    a.publicId,
                                    a.email.value,
                                    a.name.value,
                                    a.organization.value,
                                    a.status
                                )
                                from AdminAccount a
                                where a.festivalId is null
                                  and a.role is null
                                  and a.status = :status
                                  and (
                                      :keyword is null
                                      or lower(a.email.value) like concat('%', :keyword, '%')
                                      or lower(a.name.value) like concat('%', :keyword, '%')
                                      or lower(a.organization.value) like concat('%', :keyword, '%')
                                  )
                                order by a.id asc
                                """,
                        AdminSubAdminCandidateView.class
                )
                .setParameter("status", AdminStatus.ACTIVE)
                .setParameter("keyword", keyword)
                .getResultList();
    }
}
