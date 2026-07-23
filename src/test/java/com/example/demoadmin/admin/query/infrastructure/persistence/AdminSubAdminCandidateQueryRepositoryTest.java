package com.example.demoadmin.admin.query.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.admin.command.domain.vo.AdminPasswordHash;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminCandidateView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminCandidateQueryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(AdminSubAdminCandidateQueryRepositoryImpl.class)
class AdminSubAdminCandidateQueryRepositoryTest {

    @Autowired
    private AdminSubAdminCandidateQueryRepository queryRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("searchCandidates")
    class SearchCandidates {

        @Test
        @DisplayName("아직 축제에 배정되지 않은 활성 관리자만 조회한다")
        void success_SearchCandidates_ActiveUnassignedAdmins() {
            // given
            persist(admin("candidate1@mapo.go.kr", "김후보", "마포구청 소속"));
            persist(admin("candidate2@mapo.go.kr", "이후보", "서울시 소속"));
            persist(owner("owner@mapo.go.kr", 1L));
            persist(subAdmin("sub@mapo.go.kr", 1L, 1L));
            AdminAccount deleted = admin("deleted@mapo.go.kr", "박후보", "마포구청 소속");
            deleted.withdraw();
            persist(deleted);

            // when
            var result = queryRepository.searchCandidates(null);

            // then
            assertThat(result)
                    .extracting(AdminSubAdminCandidateView::email)
                    .containsExactly(
                            "candidate1@mapo.go.kr",
                            "candidate2@mapo.go.kr"
                    );
        }

        @Test
        @DisplayName("검색어가 있으면 이메일, 이름, 조직으로 필터링한다")
        void success_SearchCandidates_ByKeyword() {
            // given
            persist(admin("candidate1@mapo.go.kr", "김후보", "마포구청 소속"));
            persist(admin("candidate2@seoul.go.kr", "이검색", "서울시 소속"));

            // when
            var result = queryRepository.searchCandidates("검색");

            // then
            assertThat(result)
                    .extracting(AdminSubAdminCandidateView::email)
                    .containsExactly("candidate2@seoul.go.kr");
        }

        @Test
        @DisplayName("후보자가 없으면 빈 목록을 반환한다")
        void success_SearchCandidates_EmptyBoundary() {
            // given
            String keyword = "없음";

            // when
            var result = queryRepository.searchCandidates(keyword);

            // then
            assertThat(result).isEmpty();
        }
    }

    private AdminAccount admin(
            String email,
            String name,
            String organization
    ) {
        return AdminAccount.createAdmin(
                AdminEmail.of(email),
                AdminName.of(name),
                AdminOrganization.of(organization),
                AdminPasswordHash.of("encoded-password")
        );
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

    private AdminAccount subAdmin(
            String email,
            Long festivalId,
            Long invitedByAdminId
    ) {
        return AdminAccount.createSubAdmin(
                AdminEmail.of(email),
                AdminName.of("김관리"),
                AdminOrganization.of("마포구청 소속"),
                festivalId,
                AdminPasswordHash.of("encoded-password"),
                invitedByAdminId
        );
    }

    private AdminAccount persist(AdminAccount adminAccount) {
        entityManager.persist(adminAccount);
        entityManager.flush();
        return adminAccount;
    }
}
