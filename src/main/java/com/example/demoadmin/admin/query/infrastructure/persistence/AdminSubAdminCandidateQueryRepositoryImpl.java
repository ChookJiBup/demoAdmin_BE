package com.example.demoadmin.admin.query.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminCandidateView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminCandidateQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminSubAdminCandidateQueryRepositoryImpl
        implements AdminSubAdminCandidateQueryRepository {

    private final AdminSubAdminCandidateQueryJpaRepository jpaRepository;

    @Override
    public List<AdminSubAdminCandidateView> searchCandidates(String keyword) {
        return jpaRepository.searchCandidates(AdminStatus.ACTIVE, keyword);
    }
}
