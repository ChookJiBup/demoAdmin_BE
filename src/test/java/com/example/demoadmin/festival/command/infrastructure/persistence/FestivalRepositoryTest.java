package com.example.demoadmin.festival.command.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FestivalRepositoryTest {

    @Autowired
    private FestivalRepository festivalRepository;

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("축제 기본 정보를 저장한다")
        void success_Save() {
            // given
            Festival festival = festival();

            // when
            Festival saved = festivalRepository.save(festival);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getNameValue()).isEqualTo("마포나루 새우젓축제");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("축제 ID로 축제 기본 정보를 조회한다")
        void success_FindById() {
            // given
            Festival saved = festivalRepository.save(festival());

            // when
            var found = festivalRepository.findById(saved.getId());

            // then
            assertThat(found)
                    .isPresent()
                    .get()
                    .extracting(Festival::getNameValue)
                    .isEqualTo("마포나루 새우젓축제");
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
