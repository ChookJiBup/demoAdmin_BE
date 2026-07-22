package com.example.demoadmin.operator.query.infrastructure.persistence;

import com.example.demoadmin.operator.command.domain.FieldStaffAccount;
import com.example.demoadmin.operator.command.domain.FieldStaffStatus;
import com.example.demoadmin.operator.query.application.dto.FieldStaffView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface FieldStaffQueryJpaRepository
        extends JpaRepository<FieldStaffAccount, Long> {

    @Query("""
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
            where f.festivalId = :festivalId
              and f.status = :status
              and (
                  :keyword is null
                  or lower(f.loginId.value) like concat('%', :keyword, '%')
                  or lower(f.name.value) like concat('%', :keyword, '%')
                  or lower(f.phoneNumber.value) like concat('%', :keyword, '%')
              )
            order by f.id asc
            """)
    List<FieldStaffView> searchByFestivalIdAndStatus(
            @Param("festivalId") Long festivalId,
            @Param("status") FieldStaffStatus status,
            @Param("keyword") String keyword
    );

    @Query("""
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
            where f.festivalId = :festivalId
              and f.publicId = :publicId
              and f.status = :status
            """)
    Optional<FieldStaffView> findByFestivalIdAndPublicIdAndStatus(
            @Param("festivalId") Long festivalId,
            @Param("publicId") UUID publicId,
            @Param("status") FieldStaffStatus status
    );
}
