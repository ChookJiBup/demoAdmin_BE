package com.example.demoadmin.festival.command.infrastructure.persistence;

import com.example.demoadmin.festival.command.domain.FestivalSeries;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface FestivalSeriesJpaRepository
        extends JpaRepository<FestivalSeries, Long> {

    Optional<FestivalSeries> findByNormalizedName(String normalizedName);
}
