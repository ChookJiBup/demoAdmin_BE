package com.example.demoadmin.festival.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.admin.command.domain.AdminRole;
import com.example.demoadmin.admin.command.domain.vo.AdminEmail;
import com.example.demoadmin.admin.command.domain.vo.AdminName;
import com.example.demoadmin.admin.command.domain.vo.AdminOrganization;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.festival.command.application.dto.CreateFestivalCommand;
import com.example.demoadmin.festival.command.application.dto.UpdateFestivalCommand;
import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FestivalCommandServiceTest {

    @InjectMocks
    private FestivalCommandService festivalCommandService;

    @Mock
    private FestivalRepository festivalRepository;

    @Mock
    private AdminAccountRepository adminAccountRepository;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("축제 기본 정보를 저장하고 생성자를 1관리자로 배정한다")
        void success_Create_AssignFestivalOwner() {
            // given
            CreateFestivalCommand command = createCommand();
            AdminAccount adminAccount = unassignedAdmin();
            AdminPrincipal principal = principal(null, null);
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(adminAccount));
            given(festivalRepository.save(any(Festival.class)))
                    .willAnswer(invocation -> {
                        Festival festival = invocation.getArgument(0);
                        ReflectionTestUtils.setField(festival, "id", 1L);
                        return festival;
                    });

            // when
            Festival festival = festivalCommandService.create(command, principal);

            // then
            assertThat(festival.getNameValue()).isEqualTo(command.name());
            assertThat(festival.getStartDate()).isEqualTo(command.startDate());
            assertThat(adminAccount.getFestivalId()).isEqualTo(festival.getId());
            assertThat(adminAccount.getRole()).isEqualTo(AdminRole.FESTIVAL_OWNER);

            ArgumentCaptor<Festival> captor =
                    ArgumentCaptor.forClass(Festival.class);
            then(festivalRepository).should().save(captor.capture());
            assertThat(captor.getValue().getAddressValue())
                    .isEqualTo(command.address());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("1관리자는 담당 축제 기본 정보를 수정한다")
        void success_Update_FestivalOwner() {
            // given
            Long festivalId = 1L;
            Festival festival = festival();
            UpdateFestivalCommand command = updateCommand();
            AdminPrincipal principal = principal(
                    festivalId,
                    AdminRole.FESTIVAL_OWNER
            );
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(festivalOwner(festivalId)));
            given(festivalRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));

            // when
            Festival updated = festivalCommandService.update(
                    festivalId,
                    command,
                    principal
            );

            // then
            assertThat(updated.getNameValue()).isEqualTo(command.name());
            assertThat(updated.getAddressValue()).isEqualTo(command.address());
            then(festivalRepository).should().findById(festivalId);
        }

        @Test
        @DisplayName("서브관리자는 축제 기본 정보를 수정할 수 없다")
        void fail_Update_SubAdmin_CustomException() {
            // given
            Long festivalId = 1L;
            UpdateFestivalCommand command = updateCommand();
            AdminPrincipal principal = principal(festivalId, AdminRole.SUB_ADMIN);
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(subAdmin(festivalId)));

            // when & then
            assertThatThrownBy(() -> festivalCommandService.update(
                    festivalId,
                    command,
                    principal
            ))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.FORBIDDEN.getMessage());
        }

        @Test
        @DisplayName("1관리자는 다른 축제 기본 정보를 수정할 수 없다")
        void fail_Update_DifferentFestival_CustomException() {
            // given
            Long festivalId = 1L;
            UpdateFestivalCommand command = updateCommand();
            AdminPrincipal principal = principal(2L, AdminRole.FESTIVAL_OWNER);
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(festivalOwner(2L)));

            // when & then
            assertThatThrownBy(() -> festivalCommandService.update(
                    festivalId,
                    command,
                    principal
            ))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.FORBIDDEN.getMessage());
        }

        @Test
        @DisplayName("축제가 없으면 수정할 수 없다")
        void fail_Update_FestivalNotFound_CustomException() {
            // given
            Long festivalId = 1L;
            UpdateFestivalCommand command = updateCommand();
            AdminPrincipal principal = principal(
                    festivalId,
                    AdminRole.FESTIVAL_OWNER
            );
            given(adminAccountRepository.findById(principal.adminId()))
                    .willReturn(Optional.of(festivalOwner(festivalId)));
            given(festivalRepository.findById(festivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> festivalCommandService.update(
                    festivalId,
                    command,
                    principal
            ))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.FESTIVAL_NOT_FOUND.getMessage());
        }
    }

    private CreateFestivalCommand createCommand() {
        return new CreateFestivalCommand(
                "마포나루 새우젓축제",
                "마포구 대표 지역 축제",
                "서울특별시 마포구 월드컵로 243",
                LocalDate.of(2026, 10, 16),
                LocalDate.of(2026, 10, 18),
                LocalTime.of(10, 0),
                LocalTime.of(21, 0)
        );
    }

    private UpdateFestivalCommand updateCommand() {
        return new UpdateFestivalCommand(
                "수정 축제",
                "수정 설명",
                "서울특별시 마포구 수정로 1",
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 3),
                LocalTime.of(9, 0),
                LocalTime.of(20, 0)
        );
    }

    private AdminPrincipal principal(
            Long festivalId,
            AdminRole role
    ) {
        return new AdminPrincipal(
                1L,
                festivalId,
                "owner@mapo.go.kr",
                role
        );
    }

    private Festival festival() {
        return Festival.create(
                FestivalName.of("마포나루 새우젓축제"),
                FestivalDescription.of("마포구 대표 지역 축제"),
                FestivalAddress.of("서울특별시 마포구 월드컵로 243"),
                FestivalPeriod.of(
                        LocalDate.of(2026, 10, 16),
                        LocalDate.of(2026, 10, 18)
                ),
                FestivalOperationTime.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(21, 0)
                )
        );
    }

    private AdminAccount unassignedAdmin() {
        return AdminAccount.createAdmin(
                AdminEmail.of("owner@mapo.go.kr"),
                AdminName.of("홍길동"),
                AdminOrganization.of("마포구청 소속"),
                "encoded-password"
        );
    }

    private AdminAccount festivalOwner(Long festivalId) {
        AdminAccount adminAccount = unassignedAdmin();
        adminAccount.assignFestivalOwner(festivalId);
        return adminAccount;
    }

    private AdminAccount subAdmin(Long festivalId) {
        return AdminAccount.createSubAdmin(
                AdminEmail.of("sub@mapo.go.kr"),
                AdminName.of("김서브"),
                AdminOrganization.of("마포구청 소속"),
                festivalId,
                "encoded-password",
                1L
        );
    }
}
