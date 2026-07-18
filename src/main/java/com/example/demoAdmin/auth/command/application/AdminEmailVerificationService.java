package com.example.demoadmin.auth.command.application;

import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.api.auth.dto.AdminEmailVerificationConfirmRequest;
import com.example.demoadmin.api.auth.dto.AdminEmailVerificationRequest;
import com.example.demoadmin.auth.command.application.port.AdminEmailVerificationSender;
import com.example.demoadmin.auth.command.domain.AdminEmailVerification;
import com.example.demoadmin.auth.command.domain.AdminEmailVerificationRepository;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 관리자 회원가입 전 이메일 인증 코드 발급과 확인을 처리한다.
 */
@Service
@RequiredArgsConstructor
public class AdminEmailVerificationService {

    private static final int CODE_BOUND = 1_000_000;
    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    private final AdminAccountRepository adminAccountRepository;
    private final AdminEmailVerificationRepository verificationRepository;
    private final AdminEmailVerificationSender verificationSender;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 정부 공식 이메일인지 확인한 뒤 인증 코드를 발급한다.
     */
    public void request(AdminEmailVerificationRequest request) {
        AdminEmail email = AdminEmail.of(request.email());
        if (adminAccountRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.AUTH_EMAIL_DUPLICATED);
        }

        String code = generateCode();
        verificationRepository.save(AdminEmailVerification.issue(
                email,
                code,
                LocalDateTime.now().plus(CODE_TTL)
        ));
        verificationSender.send(email, code);
    }

    /**
     * 사용자가 입력한 이메일 인증 코드를 검증한다.
     */
    public void confirm(AdminEmailVerificationConfirmRequest request) {
        AdminEmail email = AdminEmail.of(request.email());
        AdminEmailVerification verification = verificationRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.AUTH_EMAIL_VERIFICATION_NOT_FOUND
                ));

        verification.verify(request.code(), LocalDateTime.now());
        verificationRepository.save(verification);
    }

    /**
     * 회원가입 가능 여부 판단을 위해 인증 완료 상태인지 확인한다.
     */
    public void ensureVerified(AdminEmail email) {
        AdminEmailVerification verification = verificationRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.AUTH_EMAIL_NOT_VERIFIED
                ));

        if (!verification.isVerified()) {
            throw new CustomException(ErrorCode.AUTH_EMAIL_NOT_VERIFIED);
        }
    }

    /**
     * 회원가입 성공 후 인증 완료 상태를 재사용되지 않도록 제거한다.
     */
    public void consumeVerified(AdminEmail email) {
        if (!verificationRepository.consumeVerified(email)) {
            throw new CustomException(ErrorCode.AUTH_EMAIL_NOT_VERIFIED);
        }
    }

    private String generateCode() {
        return String.format("%06d", secureRandom.nextInt(CODE_BOUND));
    }
}
