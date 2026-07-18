package com.example.demoadmin.festival.command.domain;

import java.util.Optional;

/**
 * 축제 기본 정보를 저장하는 저장소 계약이다.
 */
public interface FestivalRepository {

    /**
     * 축제 기본 정보를 저장한다.
     */
    Festival save(Festival festival);

    /**
     * 축제 ID로 축제 기본 정보를 조회한다.
     */
    Optional<Festival> findById(Long festivalId);
}
