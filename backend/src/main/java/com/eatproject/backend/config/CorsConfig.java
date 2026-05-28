package com.eatproject.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS 전역 설정
 *
 * SecurityConfig 의 http.cors(Customizer.withDefaults()) 가
 * 이 Bean 을 자동으로 참조합니다.
 *
 * application.properties 에 등록된 프론트엔드 주소를 허용:
 *   spring.react.ip=http://43.203.165.206:5173
 */
@Configuration
public class CorsConfig {

    /** application.properties: spring.react.ip */
    @Value("${spring.react.ip}")
    private String reactIp;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용 Origin: properties 값 + 로컬 개발용
        config.setAllowedOrigins(List.of(
                reactIp,                          // http://43.203.165.206:5173
                "http://localhost:5173",           // Vite 로컬 개발
                "http://localhost:3000"            // CRA 로컬 개발
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));  // JWT 토큰 헤더 노출
        config.setAllowCredentials(true);                    // 쿠키/인증 헤더 허용
        config.setMaxAge(3600L);                             // preflight 캐시 1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);    // 모든 경로에 적용
        return source;
    }
}
