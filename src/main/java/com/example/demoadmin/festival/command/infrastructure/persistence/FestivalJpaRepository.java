package com.example.demoadmin.festival.command.infrastructure.persistence;

import com.example.demoadmin.festival.command.domain.Festival;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface FestivalJpaRepository extends JpaRepository<Festival, Long> {

    Optional<Festival> findByPublicId(UUID publicId);

    boolean existsBySeriesIdAndYear(Long seriesId, int year);
}
