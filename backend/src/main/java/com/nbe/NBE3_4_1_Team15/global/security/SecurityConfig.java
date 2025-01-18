package com.nbe.NBE3_4_1_Team15.global.security;

import com.nbe.NBE3_4_1_Team15.global.rsData.RsData;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 회원가입 요청 허용
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationFilter customAuthenticationFilter) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/h2-console/**", "/api/users/register","/orders/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .headers(
                        headers->
                                headers.frameOptions(
                                        frameOptions ->
                                                frameOptions.sameOrigin()
                                )
                )
                .csrf(
                        csrf -> csrf.disable()
                )
                .addFilterBefore(
                        customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(//예외처리
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint( //인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 호출되는 핸들러
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 (인증정보 없음)
                                            response.getWriter().write(
                                                    Ut.json.toJson(
                                                            new RsData<>("401-1", "사용자 정보가 올바르지 않습니다.")
                                                    )
                                            );
                                        }
                                )
                                .accessDeniedHandler( // 인증은 되었지만 권한이 없어서 접근이 제한될 때 호출되는 핸들러
                                        (request, response, accessDeniedException) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(HttpStatus.FORBIDDEN.value()); // 403 (권한정보 없음)
                                            response.getWriter().write(
                                                    Ut.json.toJson(
                                                            new RsData<>("403-1","권한이 없습니다.")
                                                    )
                                            );
                                        }
                                )
                );

        return http.build();
    }

    //BBCryptPasswordEncoder 객체를 생성하고 스프링빈에 등록 (이해 필요)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
