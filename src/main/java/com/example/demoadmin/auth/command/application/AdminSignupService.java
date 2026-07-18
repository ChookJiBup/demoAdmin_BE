package com.example.demoadmin.auth.command.application;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.api.auth.dto.AdminSignupRequest;
import com.example.demoadmin.api.auth.dto.AdminSignupResponse;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자 회원가입 유스케이스를 처리한다.
 */
@Service
@RequiredArgsConstructor
public class AdminSignupService {

    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminEmailVerificationService emailVerificationService;

    /**
     * 회원가입 요청을 검증하고 관리자 계정을 생성한다.
     */
    @Transactional
    public AdminSignupResponse signup(AdminSignupRequest request) {
        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(ErrorCode.AUTH_PASSWORD_CONFIRM_MISMATCH);
        }

        AdminEmail email = AdminEmail.of(request.email());
        AdminName name = AdminName.of(request.name());
        AdminOrganization organization = AdminOrganization.of(request.organization());

        if (adminAccountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.AUTH_EMAIL_DUPLICATED);
        }

        AdminAccount adminAccount = AdminAccount.createAdmin(
                email,
                name,
                organization,
                AdminPasswordHash.of(passwordEncoder.encode(request.password()))
        );

        emailVerificationService.ensureVerified(email);
        AdminAccount savedAdminAccount = adminAccountRepository.save(adminAccount);
        emailVerificationService.consumeVerified(email);

        return AdminSignupResponse.from(savedAdminAccount);
    }
}

