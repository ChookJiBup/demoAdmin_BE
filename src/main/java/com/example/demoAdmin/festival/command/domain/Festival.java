package com.example.demoadmin.festival.command.domain;

import com.example.demoadmin.common.domain.BaseTimeEntity;
import com.example.demoadmin.festival.command.domain.vo.FestivalAddress;
import com.example.demoadmin.festival.command.domain.vo.FestivalDescription;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import com.example.demoadmin.festival.command.domain.vo.FestivalOperationTime;
import com.example.demoadmin.festival.command.domain.vo.FestivalPeriod;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 축제 기본 정보를 저장하는 Aggregate이다.
 */
@Entity
@Table(name = "festivals")
public class Festival extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO(festival): 현재 축제 저장 필드는 임시 기준이며 추후 DB 설계서와 API 정보를 확인한 뒤 필드/제약/응답을 수정한다.
    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "name", nullable = false, length = 100)
    )
    private FestivalName name;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "description", nullable = false, length = 1000)
    )
    private FestivalDescription description;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "address", nullable = false, length = 255)
    )
    private FestivalAddress address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "startDate",
                    column = @Column(name = "start_date", nullable = false)
            ),
            @AttributeOverride(
                    name = "endDate",
                    column = @Column(name = "end_date", nullable = false)
            )
    })
    private FestivalPeriod period;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "startTime",
                    column = @Column(name = "operation_start_time", nullable = false)
            ),
            @AttributeOverride(
                    name = "endTime",
                    column = @Column(name = "operation_end_time", nullable = false)
            )
    })
    private FestivalOperationTime operationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FestivalStatus status;

    protected Festival() {
    }

    private Festival(
            FestivalName name,
            FestivalDescription description,
            FestivalAddress address,
            FestivalPeriod period,
            FestivalOperationTime operationTime
    ) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.period = period;
        this.operationTime = operationTime;
        this.status = FestivalStatus.DRAFT;
    }

    /**
     * 임시 기준의 축제 기본 정보를 생성한다.
     */
    public static Festival create(
            FestivalName name,
            FestivalDescription description,
            FestivalAddress address,
            FestivalPeriod period,
            FestivalOperationTime operationTime
    ) {
        return new Festival(
                name,
                description,
                address,
                period,
                operationTime
        );
    }

    public Long getId() {
        return id;
    }

    public String getNameValue() {
        return name.getValue();
    }

    public String getDescriptionValue() {
        return description.getValue();
    }

    public String getAddressValue() {
        return address.getValue();
    }

    public LocalDate getStartDate() {
        return period.getStartDate();
    }

    public LocalDate getEndDate() {
        return period.getEndDate();
    }

    public LocalTime getOperationStartTime() {
        return operationTime.getStartTime();
    }

    public LocalTime getOperationEndTime() {
        return operationTime.getEndTime();
    }

    public FestivalStatus getStatus() {
        return status;
    }
}
