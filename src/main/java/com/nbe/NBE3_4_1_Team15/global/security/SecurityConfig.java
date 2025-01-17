package com.nbe.NBE3_4_1_Team15.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeHttpRequests->
                        authorizeHttpRequests
                                .requestMatchers("/h2-console/**")
                                .permitAll()
                                .anyRequest()
                                .permitAll())
                .csrf(
                        csrf->csrf.disable()
                );
        return http.build();
    }
}
