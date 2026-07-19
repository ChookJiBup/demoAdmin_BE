package com.example.demoadmin.festival.command.infrastructure.persistence;

import com.example.demoadmin.festival.command.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

interface FestivalJpaRepository extends JpaRepository<Festival, Long> {

    boolean existsBySeriesIdAndYear(Long seriesId, int year);
}
