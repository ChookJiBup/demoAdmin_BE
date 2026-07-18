# Spring Backend 테스트 및 Fixture 가이드

# 10. 테스트 구조

운영 코드의 패키지 구조를 그대로 따른다.

```text
src/test/java/<base-package>/
├── domain-a/
│   ├── command/
│   │   ├── application/
│   │   ├── domain/
│   │   └── infrastructure/
│   └── query/
│       ├── application/
│       └── repository/
├── fixtures/
└── support/
```

파일명:

```text
ResourceTest.java
ResourceServiceTest.java
ResourceServiceIntegrationTest.java
ResourceServiceConcurrencyTest.java
ResourceFacadeTest.java
ResourceRepositoryTest.java
ResourceControllerTest.java
```

테스트 메서드는 given/when/then 구조를 기본으로 작성한다.

```java
@Test
@DisplayName("대상을 생성한다")
void success_Create() {
    // given
    CreateResourceCommand command = ResourceFixture.createCommand();

    // when
    Long id = resourceService.create(command);

    // then
    assertThat(id).isNotNull();
}
```

테스트 메서드명:

- 성공: `success_[테스트할 메서드 명]_[특수 상황]`
- 실패: `fail_[테스트할 메서드 명]_[던질 에러]`
- 특수 상황이 없으면 `default`를 붙이지 않는다.
- 실패 원인을 함께 표현해야 하면 에러 타입을 마지막에 둔다. 예: `fail_Of_Null_CustomException`
- `_` 뒤에 오는 단어는 대문자로 시작한다. 예: `success_FindByEmail_IgnoreCase`
- 한 메서드 테스트가 많아지면 `@Nested`로 테스트할 메서드 단위 그룹을 만든다.
- 경계값 테스트는 항상 포함한다.

---

## 10.1 도메인 단위 테스트

Spring Context를 띄우지 않는다.

```java
class ResourceTest {

    @Nested
    @DisplayName("activate")
    class Activate {

        @Test
        @DisplayName("생성 상태에서는 활성화할 수 있다")
        void success_Activate_FromCreated() {
            // given
            Resource resource = ResourceFixture.created();

            // when
            resource.activate();

            // then
            assertThat(resource.getStatus())
                    .isEqualTo(ResourceStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        @DisplayName("완료된 대상은 취소할 수 없다")
        void fail_Cancel_CustomException() {
            // given
            Resource resource = ResourceFixture.completed();

            // when & then
            assertThatThrownBy(resource::cancel)
                    .isInstanceOf(CustomException.class)
                    .hasMessage(
                            ErrorCode.RESOURCE_CANNOT_BE_CANCELLED
                                    .getMessage()
                    );
        }
    }
}
```

---

## 10.2 Service 단위 테스트

```java
@ExtendWith(MockitoExtension.class)
class ResourceCommandServiceTest {

    @InjectMocks
    private ResourceCommandService resourceService;

    @Mock
    private ResourceRepository resourceRepository;

    private Resource resource;

    @BeforeEach
    void setUp() {
        resource = ResourceFixture.created();
    }

    @Test
    @DisplayName("대상을 생성한다")
    void success_Create() {
        // given
        given(resourceRepository.save(any(Resource.class)))
                .willReturn(resource);

        // when
        Long resourceId = resourceService.create(
                ResourceCommandFixture.create()
        );

        // then
        assertThat(resourceId).isEqualTo(resource.getId());

        then(resourceRepository)
                .should(times(1))
                .save(any(Resource.class));
    }
}
```

Mockito 스타일은 한 파일에서 통일한다.

권장:

```java
given(repository.findById(id))
        .willReturn(Optional.of(resource));

then(repository)
        .should()
        .findById(id);
```

---

## 10.3 통합 테스트

```java
@SpringBootTest
@Transactional
class ResourceServiceIntegrationTest {

    @Autowired
    private ResourceCommandService resourceService;

    @Autowired
    private ResourceJpaRepository resourceRepository;

    @Test
    @DisplayName("생성한 대상이 DB에 저장된다")
    void success_Create_Persisted() {
        // given
        CreateResourceCommand command =
                ResourceCommandFixture.create();

        // when
        Long id = resourceService.create(command);

        // then
        Resource saved = resourceRepository
                .findById(id)
                .orElseThrow();

        assertThat(saved.getName())
                .isEqualTo(command.name());
    }
}
```

검증 대상:

- 트랜잭션 경계
- JPA 매핑
- cascade와 orphanRemoval
- 실제 쿼리
- 락
- unique constraint
- Bean 조합
- Facade 전체 흐름

---

## 10.4 Repository 테스트

```java
@DataJpaTest
class ResourceQueryRepositoryTest {

    @Autowired
    private ResourceJpaRepository jpaRepository;

    @Autowired
    private ResourceQueryRepository queryRepository;

    @Test
    @DisplayName("상태와 이름 조건으로 조회한다")
    void success_Search_ByCondition() {
        // given
        jpaRepository.saveAll(
                ResourceFixture.multiple()
        );

        ResourceSearchCondition condition =
                new ResourceSearchCondition(
                        "sample",
                        ResourceStatus.ACTIVE
                );

        // when
        Page<ResourceSummary> result =
                queryRepository.search(
                        condition,
                        PageRequest.of(0, 20)
                );

        // then
        assertThat(result.getContent())
                .allMatch(item ->
                        item.status() == ResourceStatus.ACTIVE
                );
    }
}
```

검증 항목:

- null 검색 조건
- 동적 where
- 정렬
- 페이징
- 중복 행
- projection 매핑
- 데이터 없음
- 경계값

---

## 10.5 동시성 테스트

필수 요소:

- 동시에 시작하도록 동기화
- 전체 작업 완료 대기
- ExecutorService 종료
- 성공·실패 횟수 검증
- 최종 DB 상태 검증
- 예외 수집
- 운영 DB와 같은 DB 사용

```java
@SpringBootTest
class ResourceConcurrencyTest {

    private static final int THREAD_COUNT = 20;

    @Test
    @DisplayName("동시에 배정해도 한 명만 성공한다")
    void success_Assign_ConcurrencyBoundary() throws Exception {
        ExecutorService executor =
                Executors.newFixedThreadPool(THREAD_COUNT);

        CountDownLatch ready =
                new CountDownLatch(THREAD_COUNT);
        CountDownLatch start =
                new CountDownLatch(1);
        CountDownLatch done =
                new CountDownLatch(THREAD_COUNT);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    ready.countDown();

                    try {
                        start.await();
                        executeAssignment();
                        success.incrementAndGet();
                    } catch (Exception exception) {
                        failure.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await();
            start.countDown();
            done.await();

            assertThat(success.get()).isEqualTo(1);
            assertThat(failure.get())
                    .isEqualTo(THREAD_COUNT - 1);

            verifyFinalDatabaseState();
        } finally {
            executor.shutdownNow();
        }
    }
}
```

주의:

- `Thread.sleep()`을 동기화 수단으로 사용하지 않음
- H2만으로 운영 DB 락을 검증하지 않음
- Testcontainers 권장
- 작업 스레드는 테스트 트랜잭션을 공유하지 않음
- 성공 횟수뿐 아니라 최종 상태도 검증

---

# 11. Fixture

```text
fixtures/
├── ResourceFixture.java
├── UserFixture.java
├── PaymentFixture.java
└── CommandFixture.java
```

```java
public final class ResourceFixture {

    private ResourceFixture() {
    }

    public static Resource created() {
        return Resource.create(
                1L,
                "기본 이름",
                Money.of(10_000)
        );
    }

    public static Resource completed() {
        Resource resource = created();
        resource.activate();
        resource.startProcessing();
        resource.complete();
        return resource;
    }
}
```

규칙:

- 기본값은 항상 유효
- 테스트가 바꿀 값만 인자로 노출
- 상태별 Fixture 제공
- Fixture 내부 assertion 금지
- 운영 Builder를 테스트 편의 때문에 오염시키지 않음
- 비즈니스 규칙을 우회하지 않음

---

## 12. 테스트 작성 범위

Controller를 제외한 모든 운영 클래스는 단위 테스트를 작성한다.

단위 테스트 필수 대상:

- Entity와 Aggregate
- Value Object
- Domain Service
- Application Service
- Repository wrapper Service
- 외부 연동 Adapter
- 설정값 파싱이나 토큰 처리 같은 support class

통합 테스트 필수 대상:

- `[domain명]Repository`
- `[domain명]Service` 형태의 Repository wrapper Service를 제외한 모든 Service
- `[domain명][행위]Service`
- `[domain명]ApplicationService`

Repository wrapper Service는 Repository 호출을 얇게 감싸는 용도이므로
통합 테스트 필수 대상에서 제외한다.

단, Controller는 기본 필수 범위에서 제외한다.
Controller 테스트가 필요한 경우는 다음과 같다.

- API 입력 검증 자체가 복잡한 경우
- Security filter, 인증 principal, HTTP status 검증이 중요한 경우
- 프런트엔드와 API 계약을 회귀 테스트로 고정해야 하는 경우

테스트 이름과 구조는 기존 규칙을 유지한다.

- 성공: `success_[테스트할 메서드 명]_[특수 상황]`
- 실패: `fail_[테스트할 메서드 명]_[던질 에러]`
- 한 메서드 테스트가 많아지면 `@Nested`
- 경계값 테스트는 필수

---


