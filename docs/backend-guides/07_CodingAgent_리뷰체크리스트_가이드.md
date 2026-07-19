# Spring Backend Coding Agent 및 리뷰 체크리스트 가이드

# 17. Coding Agent 작업 규칙

## 탐색 순서

1. 대상 도메인 최상위 패키지
2. command/query 분리 여부
3. Aggregate와 VO
4. Application Service와 Facade
5. Repository Port
6. Infrastructure Adapter
7. `api/` 패키지의 Controller와 DTO
8. 동일 패턴 테스트
9. 공통 예외와 응답
10. 설정과 DB migration

## 구현 순서

1. 도메인 규칙과 VO
2. Repository 또는 Port 계약
3. Repository wrapper Service
4. Application Service
5. Infrastructure 구현
6. Request와 Response
7. `api/` 패키지의 Controller와 DTO
8. 단위 테스트
9. 통합 테스트
10. 동시성 테스트
11. 설정과 migration

## 금지

- 다른 도메인의 내부 package 직접 참조
- Controller에서 Repository 호출
- wrapper Service를 제외한 Service에서 Repository 직접 호출
- 도메인 내부에 HTTP Controller 추가
- Service에서 RedisTemplate 직접 조작
- Entity public setter 추가
- Entity를 API 응답으로 반환
- 예외 메시지 문자열 복제
- 기존 테스트 삭제로 빌드 통과
- 테스트 `@Disabled` 처리로 회피
- 동시성 문제를 임시 `synchronized`로 은폐
- 구조 변경과 기능 변경을 무관하게 함께 수행

---

# 18. 코드 품질

## 메서드

- 하나의 추상화 수준
- 가능한 한 20~30줄 안팎
- 인자가 많으면 Command 또는 VO 사용
- boolean 인자는 의미 있는 타입 검토
- null보다 Optional 또는 빈 컬렉션
- 예외를 정상 분기로 사용하지 않음

나쁜 예:

```java
create(
    ownerId,
    storeId,
    items,
    requestA,
    requestB,
    address,
    latitude,
    longitude,
    paymentMethod,
    estimatedTime
);
```

권장:

```java
create(new CreateResourceCommand(
        ownerId,
        storeId,
        items,
        requests,
        deliveryAddress,
        payment,
        estimatedTime
));
```

## 클래스

- 이름으로 책임 설명 가능
- public 메서드 최소화
- 의존성이 많으면 책임 분리
- 여러 도메인 wrapper Service를 다루면 Facade 검토
- `Util`, `Manager`, `Helper` 이름 남용 금지

## 주석

좋은 주석:

- 특정 락 선택 이유
- 외부 시스템 제약
- 비직관적 호환성 처리
- 임시 정책 제거 조건
- 알고리즘 전제

코드 그대로를 설명하는 주석은 쓰지 않는다.

## JavaDoc

JavaDoc은 공개 계약과 사용 의도를 설명하기 위해 작성한다.
모든 내부 구현을 반복 설명하지 않는다.

기본 원칙:

- class와 public 메서드에는 필요한 수준의 JavaDoc을 작성한다.
- 단순 getter, record accessor, 생성자 주입만 설명하는 JavaDoc은 작성하지 않는다.
- interface와 구현 클래스가 함께 있으면 기본 JavaDoc은 interface에 작성한다.
- 구현 클래스는 캐시, 외부 시스템 제약, 트랜잭션 부작용, 성능 특성처럼 구현체별로 알아야 할 내용이 있을 때만 JavaDoc을 추가한다.
- JavaDoc에는 무엇을 하는지뿐 아니라 호출자가 알아야 할 전제, 권한, 실패 조건을 우선 적는다.

예시:

```java
/**
 * 축제별 관리자 계정을 조회하고 저장하는 저장소 계약이다.
 */
public interface AdminAccountRepository {

    /**
     * 지정한 축제에 특정 관리자 역할이 이미 존재하는지 확인한다.
     */
    boolean existsByFestivalIdAndRole(Long festivalId, AdminRole role);
}
```

---

# 19. 리뷰 체크리스트

## 구조

- [ ] 최상위가 도메인 기준인가
- [ ] command와 query가 섞이지 않았는가
- [ ] domain이 infrastructure를 참조하지 않는가
- [ ] Controller와 HTTP DTO가 최상위 `api/` 패키지 아래에 있는가
- [ ] Controller가 application만 호출하는가
- [ ] 다른 도메인의 내부 구현을 직접 참조하지 않는가

## 도메인

- [ ] 상태 변경이 Aggregate 메서드인가
- [ ] public setter가 없는가
- [ ] 불변식이 도메인 내부에서 검증되는가
- [ ] VO로 만들 가치가 있는 값이 있는가
- [ ] JPA VO가 `record`가 아닌 class 기반 `@Embeddable`로 작성되었는가
- [ ] VO 내부가 아니라 embedding Entity의 `@AttributeOverride`에서 컬럼명을 지정했는가
- [ ] Aggregate 경계가 지나치게 크지 않은가
- [ ] 행사 진행자/현장 운영자를 관리자 계정 역할에 섞지 않고 operator 도메인 분리 대상으로 유지했는가

## Application

- [ ] 유스케이스 순서만 조정하는가
- [ ] 규칙이 Service에 몰리지 않았는가
- [ ] Repository 접근은 wrapper Service로만 수행하는가
- [ ] wrapper Service를 제외한 Service가 Repository를 직접 주입하지 않는가
- [ ] Facade 분리가 필요한가
- [ ] 트랜잭션 범위가 적절한가
- [ ] 외부 호출이 긴 트랜잭션 안에 있지 않은가

## Persistence

- [ ] N+1 가능성을 확인했는가
- [ ] enum이 문자열 저장인가
- [ ] 락이 보호하는 불변식이 명확한가
- [ ] unique constraint가 필요한가
- [ ] 조회 쿼리가 Command Repository를 비대하게 하지 않는가

## API

- [ ] Request와 Response가 Entity와 분리되었는가
- [ ] 검증 annotation이 입력 경계에 있는가
- [ ] 공통 응답 형식을 따르는가
- [ ] 성공 응답은 `ApiResponse.success(SuccessCode, data)`를 사용하는가
- [ ] 비즈니스 실패는 `CustomException(ErrorCode)`를 사용하는가
- [ ] ErrorCode와 SuccessCode가 `global/response/`에서 관리되는가
- [ ] 내부 예외가 노출되지 않는가
- [ ] 인증과 인가 실패가 구분되는가
- [ ] 공개 API의 class와 public 메서드에 필요한 JavaDoc이 있는가
- [ ] interface와 구현 클래스가 함께 있을 때 JavaDoc이 interface 중심으로 작성되었는가

## 테스트

- [ ] Given-When-Then 구조인가
- [ ] 테스트 메서드명이 `success_` 또는 `fail_` 규칙을 따르는가
- [ ] 한 메서드 테스트가 많을 때 `@Nested`로 묶었는가
- [ ] 경계값 테스트를 포함했는가
- [ ] 성공과 실패 경로가 모두 있는가
- [ ] Fixture를 재사용하는가
- [ ] 단위와 통합 테스트가 구분되는가
- [ ] 동시성 위험에 실제 동시성 테스트가 있는가
- [ ] 최종 상태와 호출 횟수를 검증하는가
- [ ] 운영 DB 특성이 필요하면 Testcontainers를 사용하는가

---

# 20. 구조 적용 수준

처음부터 모든 폴더를 만들 필요는 없다.

## 소규모

```text
sample/
├── application/
├── domain/
└── infrastructure/
```

## 읽기와 쓰기 복잡도가 증가한 경우

```text
sample/
├── command/
└── query/
```

## 복합 흐름과 외부 연동이 많은 경우

```text
sample/
├── command/
│   ├── application/
│   │   ├── service/
│   │   ├── facade/
│   │   └── port/
│   ├── domain/
│   └── infrastructure/
└── query/
```

폴더 구조의 목적은 빈 폴더를 미리 만드는 것이 아니다.
변경 이유가 다른 코드를 분리하고 향후 이동 비용을 낮추는 것이다.

---

# 21. 최종 요약

1. 최상위는 도메인별로 분리한다.
2. 상태 변경은 command, 조회는 query에 둔다.
3. application은 흐름을 조정하고 domain은 규칙을 가진다.
4. infrastructure는 DB, Redis, 메시지, 외부 API를 담당한다.
5. Controller와 HTTP DTO는 최상위 `api/` 패키지에 도메인별로 모은다.
6. Aggregate는 setter가 아니라 행동 메서드로 변경한다.
7. 복합 도메인 흐름은 Facade로 분리한다.
8. 조회는 전용 DTO와 Query Repository를 사용한다.
9. 단위·통합·동시성 테스트를 목적별로 구분한다.
10. 테스트 구조는 운영 코드 구조를 따른다.
11. Fixture로 유효한 기본 객체 생성을 표준화한다.
12. 락보다 먼저 보호할 불변식을 정의한다.
13. 외부 기술은 Port와 Adapter 뒤에 격리한다.
14. 새 구조보다 기존 프로젝트 패턴을 우선한다.
15. 기능 구현과 관련 테스트는 함께 변경한다.
