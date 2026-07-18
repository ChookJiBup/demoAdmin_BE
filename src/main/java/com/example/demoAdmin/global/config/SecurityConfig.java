package com.example.demoadmin.global.config;

import com.example.demoadmin.auth.support.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 관리자 API의 JWT 기반 Stateless 보안 설정을 구성한다.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 회원가입과 로그인은 공개하고 그 외 관리자 API는 인증을 요구한다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/admin/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/auth/login").permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/admin/auth/email-verification/request"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/admin/auth/email-verification/confirm"
                        ).permitAll()
                        // TODO(festival): 축제 생성 권한은 실제 DB/API 정책 확인 후 관리자 권한 기반으로 재조정한다.
                        .requestMatchers(HttpMethod.POST, "/api/festivals").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    /**
     * 관리자 비밀번호 저장에 사용할 단방향 해시 인코더를 제공한다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
