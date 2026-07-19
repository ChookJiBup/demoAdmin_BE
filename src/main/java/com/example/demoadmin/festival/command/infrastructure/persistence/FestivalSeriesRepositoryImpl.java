package com.example.demoadmin.festival.command.infrastructure.persistence;

import com.example.demoadmin.festival.command.domain.FestivalSeries;
import com.example.demoadmin.festival.command.domain.FestivalSeriesRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FestivalSeriesRepositoryImpl implements FestivalSeriesRepository {

    private final FestivalSeriesJpaRepository jpaRepository;

    @Override
    public FestivalSeries save(FestivalSeries festivalSeries) {
        return jpaRepository.save(festivalSeries);
    }

    @Override
    public Optional<FestivalSeries> findById(Long seriesId) {
        return jpaRepository.findById(seriesId);
    }

    @Override
    public Optional<FestivalSeries> findByNormalizedName(String normalizedName) {
        return jpaRepository.findByNormalizedName(normalizedName);
    }
}
