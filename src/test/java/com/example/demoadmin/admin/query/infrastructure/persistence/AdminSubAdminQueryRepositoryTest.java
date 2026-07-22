package com.example.demoadmin.admin.query.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(AdminSubAdminQueryRepositoryImpl.class)
class AdminSubAdminQueryRepositoryTest {

    @Autowired
    private AdminSubAdminQueryRepository queryRepository;

    @Autowired
    private AdminSubAdminQueryJpaRepository jpaRepository;

    @Nested
    @DisplayName("findAllByFestivalId")
    class FindAllByFestivalId {

        @Test
        @DisplayName("같은 축제의 활성 서브관리자만 조회한다")
        void success_FindAllByFestivalId_ActiveSubAdmins() {
            // given
            AdminAccount first = subAdmin("sub1@mapo.go.kr", 1L);
            AdminAccount second = subAdmin("sub2@mapo.go.kr", 1L);
            AdminAccount otherFestival = subAdmin("other@mapo.go.kr", 2L);
            AdminAccount owner = owner("owner@mapo.go.kr", 1L);
            AdminAccount deleted = subAdmin("deleted@mapo.go.kr", 1L);
            deleted.withdraw();
            jpaRepository.save(first);
            jpaRepository.save(second);
            jpaRepository.save(otherFestival);
            jpaRepository.save(owner);
            jpaRepository.save(deleted);

            // when
            var result = queryRepository.findAllByFestivalId(1L);

            // then
            assertThat(result)
                    .extracting(AdminSubAdminView::email)
                    .containsExactly("sub1@mapo.go.kr", "sub2@mapo.go.kr");
        }

        @Test
        @DisplayName("서브관리자가 없으면 빈 목록을 반환한다")
        void success_FindAllByFestivalId_EmptyBoundary() {
            // given
            Long festivalId = 1L;

            // when
            var result = queryRepository.findAllByFestivalId(festivalId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByFestivalIdAndPublicId")
    class FindByFestivalIdAndPublicId {

        @Test
        @DisplayName("같은 축제의 활성 서브관리자를 UUID로 조회한다")
        void success_FindByFestivalIdAndPublicId() {
            // given
            AdminAccount saved = jpaRepository.save(subAdmin(
                    "sub@mapo.go.kr",
                    1L
            ));

            // when
            var result = queryRepository.findByFestivalIdAndPublicId(
                    1L,
                    saved.getPublicId()
            );

            // then
            assertThat(result)
                    .isPresent()
                    .get()
                    .extracting(AdminSubAdminView::email)
                    .isEqualTo("sub@mapo.go.kr");
        }

        @Test
        @DisplayName("다른 축제의 서브관리자는 조회 결과가 없다")
        void success_FindByFestivalIdAndPublicId_DifferentFestival() {
            // given
            AdminAccount saved = jpaRepository.save(subAdmin(
                    "sub@mapo.go.kr",
                    2L
            ));

            // when
            var result = queryRepository.findByFestivalIdAndPublicId(
                    1L,
                    saved.getPublicId()
            );

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("탈퇴한 서브관리자는 조회 결과가 없다")
        void success_FindByFestivalIdAndPublicId_DeletedSubAdmin() {
            // given
            AdminAccount saved = subAdmin("sub@mapo.go.kr", 1L);
            saved.withdraw();
            jpaRepository.save(saved);

            // when
            var result = queryRepository.findByFestivalIdAndPublicId(
                    1L,
                    saved.getPublicId()
            );

            // then
            assertThat(result).isEmpty();
        }
    }

    private AdminAccount owner(String email, Long festivalId) {
        return AdminAccount.createFestivalOwner(
                AdminEmail.of(email),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                festivalId,
                AdminPasswordHash.of("encoded-password")
        );
    }

    private AdminAccount subAdmin(String email, Long festivalId) {
        return AdminAccount.createSubAdmin(
                AdminEmail.of(email),
                AdminName.of("김관리"),
                AdminOrganization.of("마포구청 소속"),
                festivalId,
                AdminPasswordHash.of("encoded-password"),
                1L
        );
    }
}
