package com.example.demoadmin.admin.command.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AdminAccountTest {

    @Nested
    @DisplayName("assignFestivalOwner")
    class AssignFestivalOwner {

        @Test
        @DisplayName("축제를 생성한 관리자를 1관리자로 배정한다")
        void success_AssignFestivalOwner() {
            // given
            AdminAccount adminAccount = adminAccount();
            Long festivalId = 1L;

            // when
            adminAccount.assignFestivalOwner(festivalId);

            // then
            assertThat(adminAccount.getFestivalId()).isEqualTo(festivalId);
            assertThat(adminAccount.getRole()).isEqualTo(AdminRole.FESTIVAL_OWNER);
            assertThat(adminAccount.canModifyFestivalInfo()).isTrue();
        }

        @Test
        @DisplayName("이미 축제에 배정된 관리자는 다시 1관리자로 배정할 수 없다")
        void fail_AssignFestivalOwner_AlreadyAssigned_CustomException() {
            // given
            AdminAccount adminAccount = adminAccount();
            adminAccount.assignFestivalOwner(1L);

            // when & then
            assertThatThrownBy(() -> adminAccount.assignFestivalOwner(2L))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.AUTH_ADMIN_ALREADY_ASSIGNED.getMessage());
        }

        @Test
        @DisplayName("축제 ID가 null이면 1관리자로 배정할 수 없다")
        void fail_AssignFestivalOwner_NullFestivalId_CustomException() {
            // given
            AdminAccount adminAccount = adminAccount();
            Long festivalId = null;

            // when & then
            assertThatThrownBy(() -> adminAccount.assignFestivalOwner(festivalId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.AUTH_ADMIN_ALREADY_ASSIGNED.getMessage());
        }
    }

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("관리자 계정을 탈퇴 상태로 변경한다")
        void success_Withdraw() {
            // given
            AdminAccount adminAccount = adminAccount();

            // when
            adminAccount.withdraw();

            // then
            assertThat(adminAccount.getStatus()).isEqualTo(AdminStatus.DELETED);
            assertThat(adminAccount.isDeleted()).isTrue();
            assertThat(adminAccount.isActive()).isFalse();
        }

        @Test
        @DisplayName("이미 탈퇴한 관리자 계정은 다시 탈퇴 처리할 수 없다")
        void fail_Withdraw_CustomException() {
            // given
            AdminAccount adminAccount = adminAccount();
            adminAccount.withdraw();

            // when & then
            assertThatThrownBy(adminAccount::withdraw)
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.AUTH_ADMIN_ALREADY_WITHDRAWN.getMessage());
        }
    }

    private AdminAccount adminAccount() {
        return AdminAccount.createAdmin(
                AdminEmail.of("owner@mapo.go.kr"),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                AdminPasswordHash.of("encoded-password")
        );
    }

    @Nested
    @DisplayName("createAdmin")
    class CreateAdmin {

        @Test
        @DisplayName("관리자 계정은 외부 노출용 UUID를 가진다")
        void success_CreateAdmin() {
            // given

            // when
            AdminAccount adminAccount = adminAccount();

            // then
            assertThat(adminAccount.getPublicId()).isNotNull();
        }

        @Test
        @DisplayName("관리자 계정은 활성 상태로 생성된다")
        void success_CreateAdmin_ActiveStatus() {
            // given

            // when
            AdminAccount adminAccount = adminAccount();

            // then
            assertThat(adminAccount.getStatus()).isEqualTo(AdminStatus.ACTIVE);
            assertThat(adminAccount.isActive()).isTrue();
        }
    }
}
