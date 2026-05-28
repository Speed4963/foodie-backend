package com.eatproject.backend.config;

import com.eatproject.backend.common.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter JwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // JWT 사용 시 비활성화 필수
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable());

        http.authorizeHttpRequests(auth -> auth
                // 1. 사전 검사 (OPTIONS 메소드 허용)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2. 모두 접근 가능한 경로
                .requestMatchers("/commu/**", "/main", "/api/member/**").permitAll()
                .requestMatchers("/api/download/**", "/images/**", "/css/**", "/js/**", "/favicon.ico","/api/admin/traffic-stats/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                .requestMatchers("/api/restaurants/**", "/images/upload", "/uploads/**").permitAll()

                // 3. EDITOR 또는 ADMIN만 접근 가능한 경로 (블로그/글쓰기 관련)
                .requestMatchers("/blog/**").hasAnyRole("EDITOR", "ADMIN")

                // 3-1. 블로그 리뷰 API (/api/posts)
                // GET(목록·상세 조회)은 비로그인도 허용, 나머지(작성·수정·삭제·좋아요)는 로그인 필요
                .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/posts/*/like").authenticated()

                // 4. 로그인한 사람만 가능한 경로
                .requestMatchers("/api/reservation/current", "/api/me").authenticated()

                // 5. 그 외 모든 요청은 로그인 필요
                .anyRequest().authenticated()
        );

        http.addFilterBefore(JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}