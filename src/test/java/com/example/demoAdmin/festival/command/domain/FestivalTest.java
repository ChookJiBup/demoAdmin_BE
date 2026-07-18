package com.example.demoadmin.festival.command.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FestivalTest {

    @Nested
    @DisplayName("updateBasicInfo")
    class UpdateBasicInfo {

        @Test
        @DisplayName("축제 기본 정보를 수정한다")
        void success_UpdateBasicInfo() {
            // given
            Festival festival = festival();

            // when
            festival.updateBasicInfo(
                    FestivalName.of("수정 축제"),
                    FestivalDescription.of("수정 설명"),
                    FestivalAddress.of("서울특별시 마포구 수정로 1"),
                    FestivalPeriod.of(
                            LocalDate.of(2026, 11, 1),
                            LocalDate.of(2026, 11, 3)
                    ),
                    FestivalOperationTime.of(
                            LocalTime.of(9, 0),
                            LocalTime.of(20, 0)
                    )
            );

            // then
            assertThat(festival.getNameValue()).isEqualTo("수정 축제");
            assertThat(festival.getDescriptionValue()).isEqualTo("수정 설명");
            assertThat(festival.getStartDate())
                    .isEqualTo(LocalDate.of(2026, 11, 1));
        }
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
}
