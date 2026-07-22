package com.example.demoadmin.admin.query.repository;

import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 축제별 서브관리자 조회 저장소 계약이다.
 */
public interface AdminSubAdminQueryRepository {

    /**
     * 지정한 축제의 활성 서브관리자 목록을 조회한다.
     */
    List<AdminSubAdminView> findAllByFestivalId(Long festivalId);

    /**
     * 지정한 축제의 활성 서브관리자를 외부 UUID로 조회한다.
     */
    Optional<AdminSubAdminView> findByFestivalIdAndPublicId(
            Long festivalId,
            UUID publicId
    );
}
