package com.example.demoadmin.admin.command.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class AdminAccountJpaRepositoryTest {

    @Autowired
    private AdminAccountJpaRepository adminAccountJpaRepository;

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("관리자 계정을 저장하고 이메일로 조회한다")
        void success_FindByEmail_IgnoreCase() {
            // given
            AdminAccount adminAccount = festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            );
            adminAccountJpaRepository.save(adminAccount);

            // when
            var found = adminAccountJpaRepository.findByEmail(
                    AdminEmail.of("OWNER@MAPO.GO.KR")
            );

            // then
            assertThat(found)
                    .isPresent()
                    .get()
                    .extracting(AdminAccount::getFestivalId)
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("저장되지 않은 이메일이면 조회 결과가 없다")
        void success_FindByEmail_NotFoundBoundary() {
            // given
            AdminEmail email = AdminEmail.of("missing@mapo.go.kr");

            // when
            var found = adminAccountJpaRepository.findByEmail(email);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("저장된 이메일이면 true를 반환한다")
        void success_ExistsByEmail_ExistingEmail() {
            // given
            adminAccountJpaRepository.save(festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            ));

            // when
            boolean exists = adminAccountJpaRepository.existsByEmail(
                    AdminEmail.of("owner@mapo.go.kr")
            );

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("저장되지 않은 이메일이면 false를 반환한다")
        void success_ExistsByEmail_NotExistingEmail() {
            // given
            AdminEmail email = AdminEmail.of("other@mapo.go.kr");

            // when
            boolean exists = adminAccountJpaRepository.existsByEmail(email);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByFestivalIdAndRole")
    class ExistsByFestivalIdAndRole {

        @Test
        @DisplayName("축제별 1관리자가 있으면 true를 반환한다")
        void success_ExistsByFestivalIdAndRole_ExistingOwner() {
            // given
            adminAccountJpaRepository.save(festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            ));

            // when
            boolean exists = adminAccountJpaRepository.existsByFestivalIdAndRole(
                    1L,
                    AdminRole.FESTIVAL_OWNER
            );

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("축제 ID 최소 경계값에서 저장된 역할이 없으면 false를 반환한다")
        void success_ExistsByFestivalIdAndRole_MinFestivalIdBoundary() {
            // given
            Long festivalId = 1L;

            // when
            boolean exists = adminAccountJpaRepository.existsByFestivalIdAndRole(
                    festivalId,
                    AdminRole.FESTIVAL_OWNER
            );

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("다른 축제에만 1관리자가 있으면 false를 반환한다")
        void success_ExistsByFestivalIdAndRole_DifferentFestival() {
            // given
            adminAccountJpaRepository.save(festivalOwner(
                    "owner@mapo.go.kr",
                    1L
            ));

            // when
            boolean exists = adminAccountJpaRepository.existsByFestivalIdAndRole(
                    2L,
                    AdminRole.FESTIVAL_OWNER
            );

            // then
            assertThat(exists).isFalse();
        }
    }

    private AdminAccount festivalOwner(String email, Long festivalId) {
        return AdminAccount.createFestivalOwner(
                AdminEmail.of(email),
                AdminName.of("홍길동"),
                AdminOrganization.of("서울시 소속"),
                festivalId,
                "{noop}password"
        );
    }
}
