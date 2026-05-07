package com.eatproject.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.react.ip}")
    private String reactIp;

    // CommonUtil에서 사용하던 파일 업로드 경로와 동일해야 합니다.
    @Value("${image.upload-dir}")
    private String uploadDir;

    // 1. CORS 설정 (기존 유지)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 리액트 주소
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name()
                )
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 2. 정적 리소스(이미지) 핸들러 설정 (추가)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /images/** 로 시작하는 주소로 접근하면
        // 서버의 실제 uploadDir 폴더 안의 파일을 찾아줌
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}