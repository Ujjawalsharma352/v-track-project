package com.vtrack.config;

import com.vtrack.security.SimpleTokenAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration: permit login and entry/exit; protect dashboard and logs with token.
 */
@Configuration
@EnableWebSecurity

public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SimpleTokenAuthFilter tokenAuthFilter) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())  // REST API - disable CSRF
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public: login and gate operations
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/entry").permitAll()
                        .requestMatchers("/api/exit/**").permitAll()
                        // Protected: dashboard & logs
                        .requestMatchers("/api/logs/**").authenticated()
                        .requestMatchers("/api/search").authenticated()
                        .requestMatchers("/api/dashboard").authenticated()
                        .requestMatchers("/api/analytics/**").authenticated()
                        .requestMatchers("/api/export").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
