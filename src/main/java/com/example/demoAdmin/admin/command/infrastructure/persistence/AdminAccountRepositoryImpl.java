package com.example.demoadmin.admin.command.infrastructure.persistence;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdminAccountRepositoryImpl implements AdminAccountRepository {

    private final AdminAccountJpaRepository jpaRepository;

    @Override
    public AdminAccount save(AdminAccount adminAccount) {
        return jpaRepository.save(adminAccount);
    }

    @Override
    public Optional<AdminAccount> findById(Long adminAccountId) {
        return jpaRepository.findById(adminAccountId);
    }

    @Override
    public boolean existsByEmail(AdminEmail email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByFestivalIdAndRole(Long festivalId, AdminRole role) {
        return jpaRepository.existsByFestivalIdAndRole(festivalId, role);
    }

    @Override
    public Optional<AdminAccount> findByEmail(AdminEmail email) {
        return jpaRepository.findByEmail(email);
    }
}
