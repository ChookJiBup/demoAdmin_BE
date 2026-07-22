package com.example.demoadmin.operator.query.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demoadmin.operator.command.domain.FieldStaffAccount;
import com.example.demoadmin.operator.command.domain.vo.FieldStaffLoginId;
import com.example.demoadmin.operator.command.domain.vo.FieldStaffName;
import com.example.demoadmin.operator.command.domain.vo.FieldStaffPasswordHash;
import com.example.demoadmin.operator.command.domain.vo.FieldStaffPhoneNumber;
import com.example.demoadmin.operator.query.application.dto.FieldStaffView;
import com.example.demoadmin.operator.query.repository.FieldStaffQueryRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(FieldStaffQueryRepositoryImpl.class)
class FieldStaffQueryRepositoryTest {

    @Autowired
    private FieldStaffQueryRepository queryRepository;

    @Autowired
    private FieldStaffQueryJpaRepository jpaRepository;

    @Nested
    @DisplayName("findAllByFestivalId")
    class FindAllByFestivalId {

        @Test
        @DisplayName("같은 축제의 활성 현장 스태프만 조회한다")
        void success_FindAllByFestivalId_ActiveFieldStaff() {
            // given
            FieldStaffAccount first = fieldStaffAccount("staff01", 1L);
            FieldStaffAccount second = fieldStaffAccount("staff02", 1L);
            FieldStaffAccount otherFestival = fieldStaffAccount("other01", 2L);
            FieldStaffAccount deleted = fieldStaffAccount("deleted01", 1L);
            deleted.delete();
            jpaRepository.save(first);
            jpaRepository.save(second);
            jpaRepository.save(otherFestival);
            jpaRepository.save(deleted);

            // when
            var result = queryRepository.findAllByFestivalId(1L);

            // then
            assertThat(result)
                    .extracting(FieldStaffView::loginId)
                    .containsExactly("staff01", "staff02");
        }

        @Test
        @DisplayName("현장 스태프가 없으면 빈 목록을 반환한다")
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
        @DisplayName("같은 축제의 활성 현장 스태프를 UUID로 조회한다")
        void success_FindByFestivalIdAndPublicId() {
            // given
            FieldStaffAccount saved = jpaRepository.save(fieldStaffAccount(
                    "staff01",
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
                    .extracting(FieldStaffView::loginId)
                    .isEqualTo("staff01");
        }

        @Test
        @DisplayName("다른 축제의 현장 스태프는 조회 결과가 없다")
        void success_FindByFestivalIdAndPublicId_DifferentFestival() {
            // given
            FieldStaffAccount saved = jpaRepository.save(fieldStaffAccount(
                    "staff01",
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
        @DisplayName("삭제된 현장 스태프는 조회 결과가 없다")
        void success_FindByFestivalIdAndPublicId_DeletedFieldStaff() {
            // given
            FieldStaffAccount saved = fieldStaffAccount("staff01", 1L);
            saved.delete();
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

    private FieldStaffAccount fieldStaffAccount(String loginId, Long festivalId) {
        return FieldStaffAccount.create(
                festivalId,
                FieldStaffLoginId.of(loginId),
                FieldStaffName.of("김스태프"),
                FieldStaffPhoneNumber.of("010-1234-5678"),
                FieldStaffPasswordHash.of("encoded-password"),
                LocalDateTime.of(2026, 10, 9, 0, 0),
                LocalDateTime.of(2026, 10, 18, 23, 59)
        );
    }
}
