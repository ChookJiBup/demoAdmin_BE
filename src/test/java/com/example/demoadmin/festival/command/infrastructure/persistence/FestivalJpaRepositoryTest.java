package com.example.demoadmin.festival.command.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalStatus;
import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class FestivalJpaRepositoryTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("축제 기본 정보를 DB에 저장한다")
        void success_Save() {
            // given
            Festival festival = festival();

            // when
            Festival saved = festivalJpaRepository.saveAndFlush(festival);
            entityManager.clear();
            Festival found = festivalJpaRepository.findById(saved.getId())
                    .orElseThrow();

            // then
            assertThat(found.getId()).isNotNull();
            assertThat(found.getNameValue()).isEqualTo("마포나루 새우젓축제");
            assertThat(found.getStatus()).isEqualTo(FestivalStatus.DRAFT);
        }

        @Test
        @DisplayName("최소 축제 기간 경계값으로 DB에 저장한다")
        void success_Save_SameDateBoundary() {
            // given
            LocalDate date = LocalDate.of(2026, 10, 16);
            Festival festival = festival(date, date);

            // when
            Festival saved = festivalJpaRepository.saveAndFlush(festival);
            entityManager.clear();
            Festival found = festivalJpaRepository.findById(saved.getId())
                    .orElseThrow();

            // then
            assertThat(found.getStartDate()).isEqualTo(date);
            assertThat(found.getEndDate()).isEqualTo(date);
        }
    }

    private Festival festival() {
        return festival(
                LocalDate.of(2026, 10, 16),
                LocalDate.of(2026, 10, 18)
        );
    }

    private Festival festival(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return Festival.create(
                FestivalName.of("마포나루 새우젓축제"),
                FestivalDescription.of("마포구 대표 지역 축제"),
                FestivalAddress.of("서울특별시 마포구 월드컵로 243"),
                FestivalPeriod.of(startDate, endDate),
                FestivalOperationTime.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(21, 0)
                )
        );
    }
}
