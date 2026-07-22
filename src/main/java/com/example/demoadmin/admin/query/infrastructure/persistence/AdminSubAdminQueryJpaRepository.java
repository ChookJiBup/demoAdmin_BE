package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AdminSubAdminQueryJpaRepository
        extends JpaRepository<AdminAccount, Long> {

    @Query("""
            select new com.example.demoadmin.admin.query.application.dto.AdminSubAdminView(
                a.publicId,
                a.email.value,
                a.name.value,
                a.organization.value,
                a.status
            )
            from AdminAccount a
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
            """)
    List<AdminSubAdminView> searchInvitedSubAdmins(
            @Param("festivalId") Long festivalId,
            @Param("invitedByAdminId") Long invitedByAdminId,
            @Param("role") AdminRole role,
            @Param("status") AdminStatus status,
            @Param("keyword") String keyword
    );

    @Query("""
            select new com.example.demoadmin.admin.query.application.dto.AdminSubAdminView(
                a.publicId,
                a.email.value,
                a.name.value,
                a.organization.value,
                a.status
            )
            from AdminAccount a
            where a.festivalId = :festivalId
              and a.invitedByAdminId = :invitedByAdminId
              and a.publicId = :publicId
              and a.role = :role
              and a.status = :status
            """)
    Optional<AdminSubAdminView> findInvitedSubAdmin(
            @Param("festivalId") Long festivalId,
            @Param("invitedByAdminId") Long invitedByAdminId,
            @Param("publicId") UUID publicId,
            @Param("role") AdminRole role,
            @Param("status") AdminStatus status
    );
}
