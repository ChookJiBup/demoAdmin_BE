package com.example.demoadmin.admin.query.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.example.demoadmin.admin.command.domain.AdminStatus;
import com.example.demoadmin.admin.query.application.dto.AdminSubAdminView;
import com.example.demoadmin.admin.query.repository.AdminSubAdminQueryRepository;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminSubAdminQueryServiceTest {

    @InjectMocks
    private AdminSubAdminQueryService queryService;

    @Mock
    private AdminSubAdminQueryRepository queryRepository;

    @Nested
    @DisplayName("findAllByFestivalId")
    class FindAllByFestivalId {

        @Test
        @DisplayName("축제 ID로 서브관리자 목록을 조회한다")
        void success_FindAllByFestivalId() {
            // given
            Long festivalId = 1L;
            AdminSubAdminView view = subAdminView();
            given(queryRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of(view));

            // when
            List<AdminSubAdminView> result =
                    queryService.findAllByFestivalId(festivalId);

            // then
            assertThat(result).containsExactly(view);
        }

        @Test
        @DisplayName("서브관리자가 없으면 빈 목록을 반환한다")
        void success_FindAllByFestivalId_EmptyBoundary() {
            // given
            Long festivalId = 1L;
            given(queryRepository.findAllByFestivalId(festivalId))
                    .willReturn(List.of());

            // when
            List<AdminSubAdminView> result =
                    queryService.findAllByFestivalId(festivalId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getByFestivalIdAndPublicId")
    class GetByFestivalIdAndPublicId {

        @Test
        @DisplayName("축제 ID와 관리자 UUID로 서브관리자를 조회한다")
        void success_GetByFestivalIdAndPublicId() {
            // given
            Long festivalId = 1L;
            UUID adminId = UUID.randomUUID();
            AdminSubAdminView view = subAdminView(adminId);
            given(queryRepository.findByFestivalIdAndPublicId(festivalId, adminId))
                    .willReturn(Optional.of(view));

            // when
            AdminSubAdminView result =
                    queryService.getByFestivalIdAndPublicId(festivalId, adminId);

            // then
            assertThat(result).isEqualTo(view);
        }

        @Test
        @DisplayName("서브관리자가 없으면 조회 실패 예외를 던진다")
        void fail_GetByFestivalIdAndPublicId_CustomException() {
            // given
            Long festivalId = 1L;
            UUID adminId = UUID.randomUUID();
            given(queryRepository.findByFestivalIdAndPublicId(festivalId, adminId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    queryService.getByFestivalIdAndPublicId(festivalId, adminId)
            )
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.ADMIN_SUB_ADMIN_NOT_FOUND.getMessage());
        }
    }

    private AdminSubAdminView subAdminView() {
        return subAdminView(UUID.randomUUID());
    }

    private AdminSubAdminView subAdminView(UUID adminId) {
        return new AdminSubAdminView(
                adminId,
                "sub@mapo.go.kr",
                "김관리",
                "마포구청 소속",
                AdminStatus.ACTIVE
        );
    }
}
