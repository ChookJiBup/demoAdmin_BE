package com.example.demoadmin.festival.query.infrastructure.persistence;

import com.example.demoadmin.festival.query.application.dto.InternalFestivalProgressStatus;
import com.example.demoadmin.festival.query.application.dto.InternalFestivalSearchCondition;
import com.example.demoadmin.festival.query.application.dto.InternalFestivalSummaryProjection;
import com.example.demoadmin.festival.query.repository.InternalFestivalQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * 사용자 서버용 축제 목록을 DTO projection으로 조회한다.
 */
@Repository
@RequiredArgsConstructor
public class InternalFestivalQueryRepositoryImpl
        implements InternalFestivalQueryRepository {

    private static final String FESTIVAL_SELECT = """
            select new com.example.demoadmin.festival.query.application.dto.InternalFestivalSummaryProjection(
                f.publicId,
                f.seriesPublicId,
                f.name.value,
                f.description.value,
                f.address.value,
                f.year,
                f.period.startDate,
                f.period.endDate,
                f.operationTime.startTime,
                f.operationTime.endTime
            )
            from Festival f
            """;

    private static final String FESTIVAL_COUNT = """
            select count(f.id)
            from Festival f
            """;

    private final EntityManager entityManager;

    @Override
    public Page<InternalFestivalSummaryProjection> searchFestivals(
            InternalFestivalSearchCondition condition,
            Pageable pageable
    ) {
        QueryParts queryParts = queryParts(condition);
        String orderBy = " order by f.period.startDate asc, f.id asc";

        TypedQuery<InternalFestivalSummaryProjection> contentQuery =
                entityManager.createQuery(
                        FESTIVAL_SELECT + queryParts.whereClause() + orderBy,
                        InternalFestivalSummaryProjection.class
                );
        bindParameters(contentQuery, queryParts.parameters());
        contentQuery.setFirstResult((int) pageable.getOffset());
        contentQuery.setMaxResults(pageable.getPageSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(
                FESTIVAL_COUNT + queryParts.whereClause(),
                Long.class
        );
        bindParameters(countQuery, queryParts.parameters());

        return new PageImpl<>(
                contentQuery.getResultList(),
                pageable,
                countQuery.getSingleResult()
        );
    }

    private QueryParts queryParts(InternalFestivalSearchCondition condition) {
        StringBuilder where = new StringBuilder(" where 1 = 1");
        Map<String, Object> parameters = new HashMap<>();

        appendProgressStatusCondition(where, parameters, condition);
        appendKeywordCondition(where, parameters, condition);

        return new QueryParts(where.toString(), parameters);
    }

    private void appendProgressStatusCondition(
            StringBuilder where,
            Map<String, Object> parameters,
            InternalFestivalSearchCondition condition
    ) {
        InternalFestivalProgressStatus status = condition.progressStatus();
        if (status == null) {
            return;
        }

        parameters.put("today", condition.today());
        if (status == InternalFestivalProgressStatus.UPCOMING) {
            where.append(" and f.period.startDate > :today");
            return;
        }
        if (status == InternalFestivalProgressStatus.ONGOING) {
            where.append("""
                     and f.period.startDate <= :today
                     and f.period.endDate >= :today
                    """);
            return;
        }
        where.append(" and f.period.endDate < :today");
    }

    private void appendKeywordCondition(
            StringBuilder where,
            Map<String, Object> parameters,
            InternalFestivalSearchCondition condition
    ) {
        if (condition.keyword() == null) {
            return;
        }

        where.append("""
                 and (
                    lower(f.name.value) like concat('%', :keyword, '%')
                    or lower(f.address.value) like concat('%', :keyword, '%')
                 )
                """);
        parameters.put("keyword", condition.keyword());
    }

    private void bindParameters(
            TypedQuery<?> query,
            Map<String, Object> parameters
    ) {
        parameters.forEach(query::setParameter);
    }

    private record QueryParts(
            String whereClause,
            Map<String, Object> parameters
    ) {
    }
}
