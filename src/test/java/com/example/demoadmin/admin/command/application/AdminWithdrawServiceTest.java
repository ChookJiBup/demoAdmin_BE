package com.example.demoadmin.admin.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminWithdrawServiceTest {

    @InjectMocks
    private AdminWithdrawService adminWithdrawService;

    @Mock
    private AdminAccountService adminAccountService;

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("인증된 관리자 계정을 탈퇴 상태로 변경한다")
        void success_Withdraw() {
            // given
            AdminAccount adminAccount = adminAccount();
            AdminPrincipal principal = new AdminPrincipal(
                    1L,
                    null,
                    "admin@mapo.go.kr",
                    null
            );
            given(adminAccountService.getById(1L)).willReturn(adminAccount);

            // when
            adminWithdrawService.withdraw(principal);

            // then
            assertThat(adminAccount.getStatus()).isEqualTo(AdminStatus.DELETED);
            then(adminAccountService).should().getById(1L);
        }

        @Test
        @DisplayName("인증 주체가 없으면 인증 예외를 던진다")
        void fail_Withdraw_CustomException() {
            // given
            AdminPrincipal principal = null;

            // when & then
            assertThatThrownBy(() -> adminWithdrawService.withdraw(principal))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.UNAUTHORIZED.getMessage());
        }
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
