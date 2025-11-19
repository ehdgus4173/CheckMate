package com.gmbbd.checkMate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIConfig {

    @Bean
    public WebClient openAiWebClient(
            @Value("${openai.api-key:TEST-KEY}") String apiKey,
            @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl) {

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
