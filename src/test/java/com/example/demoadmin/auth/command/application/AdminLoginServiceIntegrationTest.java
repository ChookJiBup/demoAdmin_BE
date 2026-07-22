package com.example.demoadmin.auth.command.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.application.AdminAccountService;
import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.api.auth.dto.AdminLoginRequest;
import com.example.demoadmin.api.auth.dto.AdminLoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdminLoginServiceIntegrationTest {

    @Autowired
    private AdminLoginService adminLoginService;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("저장된 관리자 계정으로 로그인하고 JWT를 발급한다")
        void success_Login_PersistedAdmin() {
            // given
            AdminAccount adminAccount = adminAccount("Password!123");
            adminAccountService.save(adminAccount);
            AdminLoginRequest request = new AdminLoginRequest(
                    "admin@mapo.go.kr",
                    "Password!123"
            );

            // when
            AdminLoginResponse response = adminLoginService.login(request);

            // then
            assertThat(response.accessToken()).isNotBlank();
            assertThat(response.admin().email()).isEqualTo(request.email());
            assertThat(response.admin().festivalId()).isNull();
            assertThat(response.admin().role()).isNull();
        }
    }

    private AdminAccount adminAccount(String rawPassword) {
        return AdminAccount.createAdmin(
                AdminEmail.of("admin@mapo.go.kr"),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                AdminPasswordHash.of(passwordEncoder.encode(rawPassword))
        );
    }
}
