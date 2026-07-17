# AGENTS.md

`demoAdmin`은 관광 프로젝트의 관리자용 Spring Boot 애플리케이션이다.
작업을 시작하기 전에 요청이 관리자 기능, 공통 API 계약, 빌드/설정 중 어디에 해당하는지 먼저 확인한다.

## 공통 작업 원칙

- 요청을 충족하는 최소 변경만 수행한다.
- 관광, 축제, 동선, 예측, 관리자 화면/운영 데이터 흐름을 기준으로 변경 범위를 판단한다.
- `demoUser/`와 공유되는 API 계약이나 데이터 형식이 바뀌면 사용자 앱 영향도 함께 확인한다.
- 사용자가 문서 또는 보고서 작성을 요청하면 기본적으로 `reference/YYYY-MM-DD/` 디렉터리에 Markdown 파일로 작성한다.
- 참조 문서 파일명은 한글로 작성하고 문서 성격을 접두어로 붙인다. 예: `계획서_관리자_로그인.md`, `결과보고서_관리자_로그인.md`.
- 참조 문서 서두에는 문서 형식을 명시한다. 예: `문서 형식: 계획서`, `문서 형식: 결과보고서`.
- 로컬 참조 문서와 실행 로그는 커밋 대상 코드에 남기지 않는다.
- 사용자가 만든 변경은 되돌리지 않는다.

## demoAdmin 작업 지침

- Java 코드는 기존 패키지 구조와 Spring Boot 관례를 따른다.
- Controller와 HTTP Request/Response DTO는 도메인 내부가 아니라 최상위 `api/` 패키지 아래에 도메인별로 모아 작성한다. 예: `api/auth/AdminAuthController`, `api/auth/dto/*`.
- 관리자 계정, 관리자 역할, 축제 소속 같은 관리자 자체 규칙은 `admin/` 도메인에 둔다. `auth/`는 로그인, 회원가입, JWT 발급 같은 인증 유스케이스와 인증 지원 코드만 담당한다.
- 행사 진행자, 현장 운영자, 아르바이트생은 `AdminAccount`와 `AdminRole`에 포함하지 않는다. 추후 `operator/` 도메인에서 운영 코드 기반 인증 흐름으로 분리한다.
- Repository는 도메인 계약 `Repository`, 구현체 `RepositoryImpl`, Spring Data 인터페이스 `JpaRepository`로 분리한다. 예: `AdminAccountRepository`, `AdminAccountRepositoryImpl`, `AdminAccountJpaRepository`.
- 이메일, 이름, 조직처럼 형식 검증과 의미가 있는 값은 `String`으로 직접 보관하지 않고 도메인 VO로 관리한다.
- JPA에 embedding 되는 VO는 `record`가 아니라 class 기반 `@Embeddable`로 작성한다. 기본 형태는 `@Getter`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@EqualsAndHashCode`, private 생성자, 정적 팩터리다.
- VO 내부에는 컬럼명을 고정하는 `@Column(name = ...)`을 두지 않는다. 컬럼명과 null/length 같은 DB 제약은 VO를 embedding 하는 Entity의 `@AttributeOverride`에서 지정한다.
- API 성공/실패 응답과 에러 코드는 `global/response/` 패키지의 `ApiResponse`, `SuccessCode`, `ErrorCode`, `CustomException`, `GlobalExceptionHandler`를 기준으로 관리한다.
- JPA Entity의 `createdAt`, `updatedAt`은 개별 Entity에 중복 선언하지 않고 공통 `BaseTimeEntity`를 상속해 관리한다.
- 빌드 설정은 Gradle 파일을 기준으로 확인하고, 의존성 추가는 필요한 경우에만 수행한다.
- 설정값은 `src/main/resources/application.yml`과 실행 환경 영향을 함께 확인한다.
- 운영 secret은 `application.yml`에 직접 쓰지 않고 환경 변수로 주입한다. 로컬 개발용 secret은 ignored 파일인 `src/main/resources/application-secret.yml`에만 둔다.
- 테스트를 추가하거나 수정할 때는 JUnit Platform 기반의 기존 테스트 구성을 따른다. 테스트는 given/when/then 주석 구조를 사용하고, 성공은 `success_`, 실패는 `fail_` 접두어를 사용한다. 한 메서드 테스트가 많으면 `@Nested`로 묶고 경계값 테스트를 포함한다.
- class와 public 메서드에는 필요한 수준의 JavaDoc을 작성한다.
- interface와 구현 클래스가 함께 있으면 기본 JavaDoc은 interface에 작성하고, 구현 클래스는 특수한 구현 제약이나 부작용이 있을 때만 JavaDoc을 추가한다.

## 백엔드 세부 가이드 참조 규칙

- 백엔드 구조 전반을 확인할 때는 작업 범위에 맞는 `docs/backend-guides/` 하위 세부 가이드를 읽는다.
- 아키텍처, 패키지 구조, Command/Query 분리 작업은 `docs/backend-guides/01_아키텍처_구조_가이드.md`를 읽는다.
- Application, Facade, Aggregate, Repository, Presentation 책임을 다루는 작업은 `docs/backend-guides/02_계층별_책임_가이드.md`를 읽는다.
- API 패키지, DTO, API 응답, Swagger/OpenAPI, 예외 응답 작업은 `docs/backend-guides/03_DTO_API_Swagger_예외_가이드.md`를 읽는다.
- 상태 전이, 동시성 제어, Redis, 메시지 브로커, 외부 API 작업은 `docs/backend-guides/04_도메인_동시성_외부인프라_가이드.md`를 읽는다.
- 테스트 또는 Fixture 작업은 `docs/backend-guides/05_테스트_Fixture_가이드.md`를 읽는다.
- 네이밍, Lombok, JPA, 트랜잭션, 빌드와 의존성 작업은 `docs/backend-guides/06_구현품질_JPA_트랜잭션_빌드_가이드.md`를 읽는다.
- JavaDoc 작성 기준을 확인할 때는 `docs/backend-guides/07_CodingAgent_리뷰체크리스트_가이드.md`를 읽는다.
- Coding Agent 작업 순서, 코드 품질, 리뷰 체크리스트를 확인할 때는 `docs/backend-guides/07_CodingAgent_리뷰체크리스트_가이드.md`를 읽는다.
- 한 작업이 여러 영역에 걸치면 관련 세부 가이드를 모두 읽고 적용한다.

## 지침 우선순위

더 구체적인 위치의 지침이 더 우선한다.

1. 루트의 `AGENTS.md`는 저장소 전체에 적용된다.
2. 이 파일은 `demoAdmin/` 하위 파일에 적용된다.

지침이 서로 충돌하면, 수정하려는 파일에 더 가까운 위치의 `AGENTS.md`를 우선한다.
