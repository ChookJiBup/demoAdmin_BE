# Spring Backend 구현 품질 JPA 트랜잭션 빌드 가이드

# 12. 네이밍

| 역할 | 접미사 |
| --- | --- |
| HTTP 진입점 | `Controller` |
| Repository wrapper Service | `[domain명]Service` |
| 특정 행위 유스케이스 | `[domain명][행위]Service` |
| 광범위한 유스케이스 | `[domain명]ApplicationService` |
| 조회 유스케이스 | `QueryService` |
| 복합 흐름 | `Facade` |
| 저장 계약 | `Repository` |
| JPA 저장소 | `JpaRepository` |
| 외부 구현 | `Adapter` |
| 외부 계약 | `Port` |
| HTTP 입력 | `Request` |
| HTTP 출력 | `Response` |
| 유스케이스 입력 | `Command` |
| 검색 조건 | `Condition` |
| 조회 결과 | `Detail`, `Summary`, `View` |
| 도메인 이벤트 | `Event` |
| 전송 모델 | `Message` |
| 테스트 데이터 | `Fixture` |

메서드는 의미 있는 동사로 작성한다.

`[domain명]Service`는 `[domain명]Repository`를 감싸는 wrapper Service class에만 사용한다.
실제 흐름을 담당하는 경우에는 `FestivalCreateService`, `AdminSignupService`처럼
도메인명과 행위를 함께 드러낸다.
특정 행위 하나로 좁히기 어려운 광범위한 기능은 `FestivalApplicationService`처럼 작성한다.

좋은 예:

```java
createResource()
assignAssignee()
cancelResource()
changeQuantity()
findResourceDetail()
searchResources()
markAsCompleted()
```

피할 이름:

```java
process()
handle()
doWork()
manage()
```

---

# 13. Lombok 기준

권장:

```java
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
```

주의 또는 금지:

```java
@Data
@Setter
@AllArgsConstructor
@Builder
```

Domain Entity에 `@Data`를 사용하지 않는다.

이유:

- 모든 setter 노출
- 연관관계 `toString()` 위험
- 식별자 변경 가능성
- `equals/hashCode` 문제
- 의도하지 않은 생성 경로

---

# 14. JPA 기준

- 연관관계는 기본 지연 로딩
- `createdAt`, `updatedAt`은 개별 Entity에 중복 선언하지 않고 공통 `BaseTimeEntity`를 상속해 관리한다.
- 양방향 관계는 필요할 때만 사용
- 편의 메서드로 양쪽 상태 동기화
- cascade는 Aggregate 내부에서만
- `orphanRemoval`은 생명주기 종속일 때만
- enum은 `EnumType.STRING`
- 다른 Aggregate는 ID 참조 우선 검토
- 컬렉션은 외부에서 수정 불가능하게 반환
- 의미 있는 문자열과 값은 Entity에 원시 타입으로 직접 두지 않고 VO로 감싼다.
- JPA VO는 `record`가 아니라 class 기반 `@Embeddable`로 작성하고, JPA용 protected 기본 생성자를 둔다.
- embedding 된 VO의 컬럼명과 DB 제약은 VO 내부 `@Column`이 아니라 소유 Entity의 `@AttributeOverride`에서 지정한다.

```java
public List<ResourceItem> getItems() {
    return Collections.unmodifiableList(items);
}
```

---

# 15. 트랜잭션

Command:

```java
@Transactional
```

Query:

```java
@Transactional(readOnly = true)
```

주의:

- Controller에 `@Transactional` 금지
- private 메서드 트랜잭션 기대 금지
- 같은 클래스 내부 호출의 프록시 한계 이해
- 외부 API 호출을 긴 DB 트랜잭션 안에 두지 않음
- 이벤트 발행과 DB commit 순서 고려
- 락 조회와 상태 변경은 같은 트랜잭션에서 수행

---

# 16. 빌드와 의존성

의존성은 실제 사용 여부를 기준으로 추가한다.

## 설정과 Secret

운영 secret은 Git에 커밋하지 않는다.
`application.yml`에는 환경 변수 placeholder만 둔다.

예:

```yaml
app:
  jwt:
    secret: ${APP_JWT_SECRET}
```

로컬 개발에서만 필요한 secret은 `src/main/resources/application-secret.yml`에 두고,
해당 파일은 `.gitignore`에 포함한다.

운영 배포에서는 다음 중 하나로 secret을 주입한다.

- 서버 환경 변수
- 컨테이너 환경 변수
- CI/CD secret
- Cloud Secret Manager
- Kubernetes Secret

`application-secret.yml`은 로컬 개발 편의용이며 운영 배포 산출물에 포함하지 않는다.

피할 상태:

- 여러 DB 드라이버를 이유 없이 모두 포함
- MVC와 WebFlux를 의도 없이 동시 사용
- Swagger/OpenAPI 기능 없이 문서화 의존성만 추가
- Kafka와 RabbitMQ를 실험용으로 모두 상시 포함
- 미사용 Spring Integration 모듈 다수 포함
- 테스트 라이브러리 버전 중복
- 동일 라이브러리의 서로 다른 버전 혼합

권장 절차:

1. 기능 요구 확인
2. 필요한 Starter만 추가
3. Spring dependency management 우선
4. 직접 버전 지정 이유 기록
5. API 문서화가 필요한 서비스는 `springdoc-openapi` 계열 의존성 추가
6. 테스트 의존성 중복 제거
7. 미사용 의존성 정기 점검

---

