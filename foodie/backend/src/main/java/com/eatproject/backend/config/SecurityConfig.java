package com.eatproject.backend.config;

import com.eatproject.backend.common.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // JWT 사용 시 비활성화 필수
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //테스트용 임시 허용
                .requestMatchers("/api/download/**", "/images/**", "/css/**","/js/**", "/favicon.ico").permitAll() // 이미지등은 모두 허용
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**","/v3/api-docs.yaml").permitAll()
                .requestMatchers("/api/reservation/current" , "/api/me").authenticated() //  이 주소는 로그인한 사람만!
                .requestMatchers("/main").permitAll()                                       // / (첫페이지)는 로그인 없이 모두 허용합니다.
                .anyRequest().authenticated());                                           // 위의 주소 이외의 주소는 모두 로그인해야 볼 수 있습니다.

//      4) 웹토큰 검사 필터 자동 실행
//        참고) 사용법) http.addFilterBefore(웹토큰필터, id검사필터); // id검사 필터 앞에 웹토큰필터를 넣으시오
//        용어: 시큐리티: Authentication == UserDetails == principal (사용자계정을 담는 클래스들)
        http.addFilterBefore(JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 프론트엔드 포트 허용 (3000, 5173 모두 추가)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://192.168.30.45:5173", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}