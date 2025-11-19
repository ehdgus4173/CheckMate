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
                        .title("Checkmate API")
                        .description("PDF/DOCX 요구사항 검증 서비스 API 문서")
                        .version("1.0.0")
                );
    }
}
