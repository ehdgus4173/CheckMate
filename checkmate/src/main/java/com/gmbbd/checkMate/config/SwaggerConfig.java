package com.gmbbd.checkMate.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI checkMateOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Checkmate API")                     // API 문서 제목
                        .description("PDF/DOCX 요구사항 검증 서비스 API 문서") // API 개요 설명
                        .version("1.0.0")                           // 문서 버전
                );
    }
}
