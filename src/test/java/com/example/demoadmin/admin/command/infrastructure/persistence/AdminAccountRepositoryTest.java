package com.example.demoadmin.admin.command.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdminAccountRepositoryTest {

    @Autowired
    private AdminAccountRepository adminAccountRepository;

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("관리자 계정을 저장한다")
        void success_Save() {
            // given
            AdminAccount adminAccount = festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            );

            // when
            AdminAccount saved = adminAccountRepository.save(adminAccount);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getEmailValue()).isEqualTo("owner@mapo.go.kr");
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("이메일로 관리자 계정을 조회한다")
        void success_FindByEmail() {
            // given
            adminAccountRepository.save(festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            ));

            // when
            var found = adminAccountRepository.findByEmail(
                    AdminEmail.of("owner@mapo.go.kr")
            );

            // then
            assertThat(found)
                    .isPresent()
                    .get()
                    .extracting(AdminAccount::getFestivalId)
                    .isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("existsByFestivalIdAndRole")
    class ExistsByFestivalIdAndRole {

        @Test
        @DisplayName("축제별 역할 존재 여부를 확인한다")
        void success_ExistsByFestivalIdAndRole() {
            // given
            adminAccountRepository.save(festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            ));

            // when
            boolean exists = adminAccountRepository.existsByFestivalIdAndRole(
                    1L,
                    AdminRole.FESTIVAL_OWNER
            );

            // then
            assertThat(exists).isTrue();
        }
    }

    private AdminAccount festivalOwner(
            String email,
            Long festivalId
    ) {
        return AdminAccount.createFestivalOwner(
                AdminEmail.of(email),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                festivalId,
                AdminPasswordHash.of("encoded-password")
        );
    }
}
