package com.nbe.NBE3_4_1_Team15.global.security;

import com.nbe.NBE3_4_1_Team15.global.rsData.RsData;
import com.nbe.NBE3_4_1_Team15.global.security.CustomAuthenticationFilter;
import com.nbe.NBE3_4_1_Team15.standard.util.Ut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationFilter customAuthenticationFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/h2-console/**", "/api/users/register", "/api/v1/products", "/admin/orders/**", "/user/orders/**")
                                .permitAll() // 특정 경로는 인증 없이 허용
                                .requestMatchers("/api/*/members/login", "/api/*/members/logout", "/api/*/members/join")
                                .permitAll() // 로그인, 로그아웃, 회원가입은 인증 없이 허용
                                .requestMatchers("/api/v1/members/refresh")
                                .permitAll()
                                .anyRequest()
                                .authenticated()) // 나머지 모든 요청은 인증 필요
                .headers(headers ->
                        headers.frameOptions(frameOptions ->
                                frameOptions.sameOrigin())) // H2 콘솔 접속 허용
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 커스텀 인증 필터 추가
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=utf-8");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.getWriter().write(Ut.json.toJson(new RsData<>("401-1", "사용자 정보가 올바르지 않습니다.")));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=utf-8");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.getWriter().write(Ut.json.toJson(new RsData<>("403-1", "권한이 없습니다.")));
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화에 BCrypt 사용
    }

    // CORS 설정 추가
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // 프론트엔드 주소 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 설정 적용
        return source;
    }
}
