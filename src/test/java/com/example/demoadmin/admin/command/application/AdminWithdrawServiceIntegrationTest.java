package com.example.demoadmin.admin.command.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.auth.support.AdminPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdminWithdrawServiceIntegrationTest {

    @Autowired
    private AdminWithdrawService adminWithdrawService;

    @Autowired
    private AdminAccountService adminAccountService;

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("관리자 계정 상태를 탈퇴로 저장한다")
        void success_Withdraw_Persisted() {
            // given
            AdminAccount saved = adminAccountService.save(adminAccount());
            AdminPrincipal principal = new AdminPrincipal(
                    saved.getId(),
                    null,
                    saved.getEmailValue(),
                    null
            );

            // when
            adminWithdrawService.withdraw(principal);

            // then
            AdminAccount found = adminAccountService.getById(saved.getId());
            assertThat(found.getStatus()).isEqualTo(AdminStatus.DELETED);
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
