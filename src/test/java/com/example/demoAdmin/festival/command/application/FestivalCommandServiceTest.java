package com.example.demoadmin.festival.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.demoadmin.festival.command.application.dto.CreateFestivalCommand;
import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FestivalCommandServiceTest {

    @InjectMocks
    private FestivalCommandService festivalCommandService;

    @Mock
    private FestivalRepository festivalRepository;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("축제 기본 정보를 저장한다")
        void success_Create() {
            // given
            CreateFestivalCommand command = createCommand();
            given(festivalRepository.save(any(Festival.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            Festival festival = festivalCommandService.create(command);

            // then
            assertThat(festival.getNameValue()).isEqualTo(command.name());
            assertThat(festival.getStartDate()).isEqualTo(command.startDate());

            ArgumentCaptor<Festival> captor =
                    ArgumentCaptor.forClass(Festival.class);
            then(festivalRepository).should().save(captor.capture());
            assertThat(captor.getValue().getAddressValue())
                    .isEqualTo(command.address());
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
}
