package com.example.demoadmin.api.auth;

import com.example.demoadmin.api.auth.dto.AdminLoginRequest;
import com.example.demoadmin.api.auth.dto.AdminLoginResponse;
import com.example.demoadmin.api.auth.dto.AdminSignupRequest;
import com.example.demoadmin.api.auth.dto.AdminSignupResponse;
import com.example.demoadmin.auth.command.application.AdminLoginService;
import com.example.demoadmin.auth.command.application.AdminSignupService;
import com.example.demoadmin.global.response.ApiResponse;
import com.example.demoadmin.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 회원가입과 로그인 API를 제공한다.
 */
@Tag(name = "Admin Auth", description = "관리자 회원가입 및 JWT 로그인 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminSignupService adminSignupService;
    private final AdminLoginService adminLoginService;

    /**
     * 축제별 1관리자 계정을 생성한다.
     */
    @Operation(summary = "축제 1관리자 회원가입")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/auth/signup")
    public ApiResponse<AdminSignupResponse> signup(
            @Valid @RequestBody AdminSignupRequest request
    ) {
        return ApiResponse.success(
                SuccessCode.ADMIN_SIGNUP_SUCCESS,
                adminSignupService.signup(request)
        );
    }

    /**
     * 관리자 이메일과 비밀번호를 검증하고 JWT를 발급한다.
     */
    @Operation(summary = "관리자 로그인")
    @PostMapping("/auth/login")
    public ApiResponse<AdminLoginResponse> login(
            @Valid @RequestBody AdminLoginRequest request
    ) {
        return ApiResponse.success(
                SuccessCode.ADMIN_LOGIN_SUCCESS,
                adminLoginService.login(request)
        );
    }

}

