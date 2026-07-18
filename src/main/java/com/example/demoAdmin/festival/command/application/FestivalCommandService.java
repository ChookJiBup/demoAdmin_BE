package com.example.demoadmin.festival.command.application;

import com.example.demoadmin.festival.command.application.dto.CreateFestivalCommand;
import com.example.demoadmin.festival.command.application.dto.UpdateFestivalCommand;
import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
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

    /**
     * 1관리자 권한으로 담당 축제의 기본 정보를 수정한다.
     */
    public Festival update(
            Long festivalId,
            UpdateFestivalCommand command,
            AdminPrincipal principal
    ) {
        validateFestivalOwner(festivalId, principal);

        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(ErrorCode.FESTIVAL_NOT_FOUND));
        festival.updateBasicInfo(
                FestivalName.of(command.name()),
                FestivalDescription.of(command.description()),
                FestivalAddress.of(command.address()),
                FestivalPeriod.of(command.startDate(), command.endDate()),
                FestivalOperationTime.of(
                        command.operationStartTime(),
                        command.operationEndTime()
                )
        );

        return festival;
    }

    private void validateFestivalOwner(
            Long festivalId,
            AdminPrincipal principal
    ) {
        if (principal == null
                || !principal.role().canModifyFestivalInfo()
                || !festivalId.equals(principal.festivalId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
