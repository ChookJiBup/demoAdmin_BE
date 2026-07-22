package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminCandidateView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AdminSubAdminCandidateQueryJpaRepository
        extends JpaRepository<AdminAccount, Long> {

    @Query("""
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
            """)
    List<AdminSubAdminCandidateView> searchCandidates(
            @Param("status") AdminStatus status,
            @Param("keyword") String keyword
    );
}
