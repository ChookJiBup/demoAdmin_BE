package com.example.demoadmin.global.response;

/**
 * API 성공 상황별 숫자 코드와 메시지를 정의한다.
 */
public enum SuccessCode {
    OK(20000, "요청이 성공적으로 처리되었습니다."),
    ADMIN_SIGNUP_SUCCESS(21000, "관리자 회원가입이 완료되었습니다."),
    ADMIN_LOGIN_SUCCESS(21001, "관리자 로그인에 성공했습니다."),
    ADMIN_EMAIL_VERIFICATION_REQUEST_SUCCESS(21002, "관리자 이메일 인증 코드가 발송되었습니다."),
    ADMIN_EMAIL_VERIFICATION_CONFIRM_SUCCESS(21003, "관리자 이메일 인증이 완료되었습니다."),
    FESTIVAL_CREATE_SUCCESS(22000, "축제 기본 정보가 저장되었습니다."),
    FESTIVAL_UPDATE_SUCCESS(22001, "축제 기본 정보가 수정되었습니다."),
    FESTIVAL_DASHBOARD_READ_SUCCESS(23000, "축제 대시보드 조회가 완료되었습니다."),
    FESTIVAL_REPORT_SUMMARY_READ_SUCCESS(24000, "축제 결과 보고서 요약 조회가 완료되었습니다.");

    private final int code;
    private final String message;

    SuccessCode(
            int code,
            String message
    ) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
