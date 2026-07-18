package com.example.demoadmin.festival.command.application;

import com.example.demoadmin.festival.command.application.dto.CreateFestivalCommand;
import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 축제 기본 정보 생성 유스케이스를 조정한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FestivalCommandService {

    private final FestivalRepository festivalRepository;

    /**
     * 임시 기준의 축제 기본 정보를 저장한다.
     */
    public Festival create(CreateFestivalCommand command) {
        Festival festival = Festival.create(
                FestivalName.of(command.name()),
                FestivalDescription.of(command.description()),
                FestivalAddress.of(command.address()),
                FestivalPeriod.of(command.startDate(), command.endDate()),
                FestivalOperationTime.of(
                        command.operationStartTime(),
                        command.operationEndTime()
                )
        );

        return festivalRepository.save(festival);
    }
}
