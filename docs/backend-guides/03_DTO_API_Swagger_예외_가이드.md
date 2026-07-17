# Spring Backend DTO API Swagger 예외 가이드

# 5. DTO 작성 방식

## API 패키지 위치

Controller와 HTTP Request/Response DTO는 최상위 `api/` 패키지 아래에 둔다.
도메인별 API 파일은 `api/<domain>/` 아래에 모아서 관리한다.

권장 구조:

```text
api/
└── auth/
    ├── AdminAuthController.java
    └── dto/
        ├── AdminSignupRequest.java
        ├── AdminSignupResponse.java
        ├── AdminLoginRequest.java
        └── AdminLoginResponse.java
```

도메인 패키지 안에는 HTTP Controller를 두지 않는다.
도메인 패키지는 application, domain, infrastructure, support 책임을 우선한다.

## Request와 Command 분리

Request, Response, Command 같은 경계 DTO는 Java `record`를 사용할 수 있다.
다만 JPA에 embedding 되는 도메인 VO는 record가 아니라 class 기반 `@Embeddable`로 작성한다.

```java
public record CreateResourceRequest(
        @NotBlank String name,
        @Positive long amount
) {

    public CreateResourceCommand toCommand(Long ownerId) {
        return new CreateResourceCommand(
                ownerId,
                name,
                amount
        );
    }
}
```

```java
public record CreateResourceCommand(
        Long ownerId,
        String name,
        long amount
) {
}
```

이유:

- HTTP 외 입력 경로 재사용
- Application이 Jackson에 종속되지 않음
- API 필드명 변경 영향 축소
- 테스트 입력 생성 단순화

## Entity 응답 금지

Entity를 API 응답으로 직접 반환하지 않는다.

문제:

- 지연 로딩 직렬화
- 내부 필드 노출
- 순환 참조
- DB 구조와 API 결합
- 개인정보 노출 가능성

```java
public record ResourceDetailResponse(
        Long id,
        String name,
        String status,
        long amount
) {

    public static ResourceDetailResponse from(
            ResourceDetail detail
    ) {
        return new ResourceDetailResponse(
                detail.id(),
                detail.name(),
                detail.status().name(),
                detail.amount()
        );
    }
}
```

## Swagger/OpenAPI 문서화

외부 또는 프런트엔드에서 호출하는 API를 추가할 때는
Swagger/OpenAPI 문서도 함께 작성한다.

권장 기준:

- `api/` 패키지의 Controller 단위로 API 목적과 태그를 명시한다.
- Request와 Response DTO에는 필요한 설명과 예시를 작성한다.
- 인증이 필요한 API는 Bearer JWT 인증 요구사항을 문서에 표시한다.
- 내부 구현 클래스, Entity, Repository 구조가 문서에 노출되지 않도록 한다.
- 실제 API 계약과 Swagger 문서가 달라지지 않도록 API 변경 시 함께 수정한다.

Swagger 문서는 API 계약 확인용이며,
도메인 규칙이나 상세 비즈니스 설명을 대신하지 않는다.

---

# 6. 예외와 응답

공통 API 응답과 예외 처리는 `global/response/` 패키지에서 관리한다.

권장 구조:

```text
global/
└── response/
    ├── ApiResponse.java
    ├── SuccessCode.java
    ├── ErrorCode.java
    ├── CustomException.java
    └── GlobalExceptionHandler.java
```

성공 응답은 `SuccessCode`와 함께 `ApiResponse.success(...)`로 감싼다.

```java
return ApiResponse.success(
        SuccessCode.RESOURCE_CREATE_SUCCESS,
        response
);
```

비즈니스 실패는 문자열 메시지를 직접 던지지 않고 `CustomException`과 `ErrorCode`를 사용한다.

```java
public enum ErrorCode {

    RESOURCE_NOT_FOUND(
            40401,
            HttpStatus.NOT_FOUND,
            "대상을 찾을 수 없습니다."
    ),

    INVALID_RESOURCE_STATUS(
            40901,
            HttpStatus.CONFLICT,
            "현재 상태에서는 처리할 수 없습니다."
    );

    private final int code;
    private final HttpStatus status;
    private final String message;
}
```

```java
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(
            CustomException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }
}
```

규칙:

- 비즈니스 실패와 시스템 실패 구분
- 내부 예외 메시지 직접 노출 금지
- 동일 실패는 동일 ErrorCode 사용
- 문자열 예외 메시지 중복 금지
- 인증 실패와 인가 실패 구분
- 성공 응답은 SuccessCode 사용
- 실패 응답은 ErrorCode와 CustomException 사용

---

