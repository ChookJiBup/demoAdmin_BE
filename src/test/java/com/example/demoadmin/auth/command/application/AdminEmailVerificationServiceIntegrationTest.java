package com.example.demoadmin.auth.command.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.api.auth.dto.AdminEmailVerificationRequest;
import com.example.demoadmin.auth.command.application.port.AdminEmailVerificationSender;
import com.example.demoadmin.auth.command.domain.AdminEmailVerification;
import com.example.demoadmin.auth.command.domain.AdminEmailVerificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AdminEmailVerificationServiceIntegrationTest {

    @Autowired
    private AdminEmailVerificationService emailVerificationService;

    @MockitoBean
    private AdminAccountRepository adminAccountRepository;

    @MockitoBean
    private AdminEmailVerificationRepository verificationRepository;

    @MockitoBean
    private AdminEmailVerificationSender verificationSender;

    @Nested
    @DisplayName("request")
    class Request {

        @Test
        @DisplayName("이메일 인증 요청을 저장하고 인증 코드를 발송한다")
        void success_Request() {
            // given
            String email = "admin@mapo.go.kr";
            AdminEmail adminEmail = AdminEmail.of(email);
            given(adminAccountRepository.existsByEmail(adminEmail))
                    .willReturn(false);

            // when
            emailVerificationService.request(
                    new AdminEmailVerificationRequest(email)
            );

            // then
            ArgumentCaptor<AdminEmailVerification> captor =
                    ArgumentCaptor.forClass(AdminEmailVerification.class);
            then(verificationRepository).should().save(captor.capture());
            then(verificationSender).should().send(
                    adminEmail,
                    captor.getValue().getCode()
            );
        }
    }
}
