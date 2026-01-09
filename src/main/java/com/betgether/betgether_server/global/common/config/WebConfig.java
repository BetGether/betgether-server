package com.betgether.betgether_server.global.common.config;

import com.betgether.betgether_server.domain.auth.controller.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**").excludePathPatterns("/api/auth/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 패턴 기반 허용을 사용하되, 보안과 디버깅을 위해 로컬과 버셀 주소는 주석으로라도 명시해두는 것이 정석입니다.
                .allowedOriginPatterns(
                        "http://localhost:3000",
                        "http://localhost:5173",
                        "https://betgether-api.shop",
                        "https://www.betgether-api.shop",
                        "https://betgether-client.vercel.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("*") // 모든 도메인을 허용 (패턴 기반)
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//
////        registry.addMapping("/**")
////                .allowedOrigins(
////                        "http://localhost:3000",
////                        "http://localhost:5173",
////                        "https://betgether-client.vercel.app"
////                )
////                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
////                .allowedHeaders("*")
////                .allowCredentials(true);
//    }
}
