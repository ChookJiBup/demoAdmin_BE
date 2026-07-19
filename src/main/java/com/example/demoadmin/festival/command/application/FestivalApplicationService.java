package com.example.demoadmin.festival.command.application;

import com.example.demoadmin.admin.command.domain.AdminAccount;
import com.example.demoadmin.admin.command.domain.AdminAccountRepository;
import com.example.demoadmin.auth.support.AdminPrincipal;
import com.example.demoadmin.festival.command.application.dto.CreateFestivalCommand;
import com.example.demoadmin.festival.command.application.dto.UpdateFestivalCommand;
import com.example.demoadmin.festival.command.domain.Festival;
import com.example.demoadmin.festival.command.domain.FestivalRepository;
import com.example.demoadmin.festival.command.domain.FestivalSeries;
import com.example.demoadmin.festival.command.domain.FestivalSeriesRepository;
import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import com.example.demoadmin.global.response.CustomException;
import com.example.demoadmin.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 축제 기본 정보 생성과 수정 유스케이스를 조정한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FestivalApplicationService {

    private final FestivalRepository festivalRepository;
    private final FestivalSeriesRepository festivalSeriesRepository;
    private final AdminAccountRepository adminAccountRepository;

    /**
     * 축제 묶음을 연결한 뒤 연도별 축제 기본 정보를 저장하고 생성자를 1관리자로 배정한다.
     */
    public Festival create(
            CreateFestivalCommand command,
            AdminPrincipal principal
    ) {
        AdminAccount creator = findAuthenticatedAdmin(principal);
        FestivalName name = FestivalName.of(command.name());
        FestivalPeriod period = FestivalPeriod.of(
                command.startDate(),
                command.endDate()
        );
        FestivalSeries series = findOrCreateSeries(command.seriesId(), name);
        validateUniqueFestivalYear(series.getId(), period.getStartDate().getYear());

        Festival festival = Festival.create(
                series.getId(),
                name,
                FestivalDescription.of(command.description()),
                FestivalAddress.of(command.address()),
                period,
                FestivalOperationTime.of(
                        command.operationStartTime(),
                        command.operationEndTime()
                )
        );

        Festival savedFestival = festivalRepository.save(festival);
        creator.assignFestivalOwner(savedFestival.getId());

        return savedFestival;
    }

    private FestivalSeries findOrCreateSeries(
            Long seriesId,
            FestivalName name
    ) {
        if (seriesId != null) {
            return festivalSeriesRepository.findById(seriesId)
                    .orElseThrow(() -> new CustomException(
                            ErrorCode.FESTIVAL_SERIES_NOT_FOUND
                    ));
        }

        String normalizedName = FestivalSeries.normalize(name);
        return festivalSeriesRepository.findByNormalizedName(normalizedName)
                .orElseGet(() -> festivalSeriesRepository.save(
                        FestivalSeries.create(name)
                ));
    }

    private void validateUniqueFestivalYear(
            Long seriesId,
            int year
    ) {
        if (festivalRepository.existsBySeriesIdAndYear(seriesId, year)) {
            throw new CustomException(ErrorCode.FESTIVAL_YEAR_ALREADY_EXISTS);
        }
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
        AdminAccount adminAccount = findAuthenticatedAdmin(principal);
        if (!adminAccount.canModifyFestivalInfo()
                || !festivalId.equals(adminAccount.getFestivalId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private AdminAccount findAuthenticatedAdmin(AdminPrincipal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return adminAccountRepository.findById(principal.adminId())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }
}
