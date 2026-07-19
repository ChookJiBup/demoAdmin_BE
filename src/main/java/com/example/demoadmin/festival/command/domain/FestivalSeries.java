package com.example.demoadmin.festival.command.domain;

import com.example.demoadmin.common.domain.BaseTimeEntity;
import com.example.demoadmin.festival.command.domain.vo.FestivalName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 같은 축제를 연도별 개최 회차로 묶기 위한 상위 Aggregate이다.
 */
@Entity
@Getter
@Table(
        name = "festival_series",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_festival_series_normalized_name",
                columnNames = "normalized_name"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FestivalSeries extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "name", nullable = false, length = 100)
    )
    private FestivalName name;

    @Column(name = "normalized_name", nullable = false, length = 100)
    private String normalizedName;

    private FestivalSeries(FestivalName name) {
        this.name = name;
        this.normalizedName = normalize(name);
    }

    /**
     * 같은 축제의 연도별 개최 회차를 묶는 축제 시리즈를 생성한다.
     */
    public static FestivalSeries create(FestivalName name) {
        return new FestivalSeries(name);
    }

    /**
     * 축제명 기반 자동 연결에 사용할 정규화 이름을 만든다.
     */
    public static String normalize(FestivalName name) {
        return name.getValue()
                .replaceAll("\\s+", "")
                .toLowerCase(Locale.ROOT);
    }

    public String getNameValue() {
        return name.getValue();
    }
}
