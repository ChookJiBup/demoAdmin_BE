package com.example.demoadmin.festival.command.infrastructure.persistence;

import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FestivalRepositoryImpl implements FestivalRepository {

    private final FestivalJpaRepository jpaRepository;

    @Override
    public Festival save(Festival festival) {
        return jpaRepository.save(festival);
    }

    @Override
    public Optional<Festival> findById(Long festivalId) {
        return jpaRepository.findById(festivalId);
    }
}
