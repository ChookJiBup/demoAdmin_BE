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

    /**
     * 지정한 축제 묶음의 특정 개최 연도 축제가 이미 존재하는지 확인한다.
     */
    boolean existsBySeriesIdAndYear(Long seriesId, int year);
}
