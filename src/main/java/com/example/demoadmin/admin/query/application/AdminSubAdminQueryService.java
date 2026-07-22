package com.example.demoadmin.admin.query.application;

import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminQueryRepository;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 서브관리자 조회 Repository 접근을 감싸는 wrapper Service이다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSubAdminQueryService {

    private final AdminSubAdminQueryRepository queryRepository;

    /**
     * 지정한 축제의 활성 서브관리자 목록을 조회한다.
     */
    public List<AdminSubAdminView> findAllByFestivalId(Long festivalId) {
        return queryRepository.findAllByFestivalId(festivalId);
    }

    /**
     * 지정한 축제의 활성 서브관리자를 외부 UUID로 조회한다.
     */
    public AdminSubAdminView getByFestivalIdAndPublicId(
            Long festivalId,
            UUID publicId
    ) {
        return queryRepository.findByFestivalIdAndPublicId(festivalId, publicId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_SUB_ADMIN_NOT_FOUND));
    }
}
