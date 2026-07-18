package com.example.demoadmin.auth.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.api.auth.dto.AdminLoginRequest;
import com.example.demoadmin.api.auth.dto.AdminLoginResponse;
import com.example.demoadmin.auth.command.infrastructure.JwtTokenProvider;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminLoginServiceTest {

    @InjectMocks
    private AdminLoginService adminLoginService;

    @Mock
    private AdminAccountRepository adminAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("관리자 이메일과 비밀번호가 일치하면 JWT를 발급한다")
        void success_Login() {
            // given
            AdminLoginRequest request = loginRequest();
            AdminAccount adminAccount = adminAccount();
            given(adminAccountRepository.findByEmail(AdminEmail.of(request.email())))
                    .willReturn(Optional.of(adminAccount));
            given(passwordEncoder.matches(
                    request.password(),
                    adminAccount.getPasswordHashValue()
            )).willReturn(true);
            given(jwtTokenProvider.createAccessToken(adminAccount))
                    .willReturn("access-token");
            given(jwtTokenProvider.getAccessTokenExpirationSeconds())
                    .willReturn(1800L);

            // when
            AdminLoginResponse response = adminLoginService.login(request);

            // then
            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.expiresIn()).isEqualTo(1800L);
            assertThat(response.admin().email()).isEqualTo(request.email());
        }

        @Test
        @DisplayName("이메일에 해당하는 계정이 없으면 로그인할 수 없다")
        void fail_Login_InvalidCredentials_CustomException() {
            // given
            AdminLoginRequest request = loginRequest();
            given(adminAccountRepository.findByEmail(AdminEmail.of(request.email())))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminLoginService.login(request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.AUTH_INVALID_CREDENTIALS.getMessage());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 로그인할 수 없다")
        void fail_Login_PasswordMismatch_CustomException() {
            // given
            AdminLoginRequest request = loginRequest();
            AdminAccount adminAccount = adminAccount();
            given(adminAccountRepository.findByEmail(AdminEmail.of(request.email())))
                    .willReturn(Optional.of(adminAccount));
            given(passwordEncoder.matches(
                    request.password(),
                    adminAccount.getPasswordHashValue()
            )).willReturn(false);

            // when & then
            assertThatThrownBy(() -> adminLoginService.login(request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.AUTH_INVALID_CREDENTIALS.getMessage());
        }
    }

    private AdminLoginRequest loginRequest() {
        return new AdminLoginRequest(
                "admin@mapo.go.kr",
                "Password!123"
        );
    }

    private AdminAccount adminAccount() {
        return AdminAccount.createAdmin(
                AdminEmail.of("admin@mapo.go.kr"),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                AdminPasswordHash.of("encoded-password")
        );
    }
}
