package com.gmbbd.checkMate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmbbd.checkMate.model.EvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {

    private final WebClient openAiWebClient;   // OpenAIConfig에서 주입
    private final ObjectMapper objectMapper;   // 스프링 Bean 재사용

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Override
    public EvaluationResult evaluateRequirement(String requirementText, String documentText) {
        // 1) 프롬프트 생성 (템플릿 + 공백 정리)
        String prompt = buildPrompt(requirementText, documentText);

        // 2) OpenAI 요청 Payload
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "temperature", 0,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        // 3) WebClient 호출
        String rawResponse = openAiWebClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e ->
                        Mono.error(new RuntimeException("OpenAI API 호출 실패: " + e.getMessage(), e))
                )
                .block();

        // 4) JSON 파싱 → EvaluationResult 변환
        return parseEvaluationResult(rawResponse, requirementText, null);
    }

    private String buildPrompt(String req, String submission) {
        String safeReq = (req == null) ? "" : req.trim();
        String safeSubmission = (submission == null) ? "" : submission;

        // 불필요한 공백/개행 정리 (내용 누락 없이 정규화만)
        String normalizedReq = normalizeText(safeReq);
        String normalizedSubmission = normalizeText(safeSubmission);

        try {
            ClassPathResource resource =
                    new ClassPathResource("prompts/llm_prompt.txt");

            try (InputStream is = resource.getInputStream()) {
                String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                // 템플릿의 %s 두 군데에 순서대로 채움: 요구사항, 문서내용
                return template.formatted(normalizedReq, normalizedSubmission);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "프롬프트 템플릿(prompts/llm_prompt.txt)을 읽는 데 실패했습니다.", e
            );
        }
    }

    /**
     * 여러 개의 공백/개행/탭을 하나의 공백으로 줄이고, 양 끝 공백을 제거.
     * 내용 자체는 자르지 않고, 표현만 정리한다.
     */
    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * OpenAI 응답(JSON 문자열)을 파싱해서 EvaluationResult로 변환.
     * content 안에 LLM이 반환한 JSON이 들어 있다고 가정한다.
     *
     * 기대 JSON 예시:
     * {
     *   "status": "FULFILLED",
     *   "score": 0.85,
     *   "matchedKeywordCount": 5,
     *   "totalKeywordCount": 6,
     *   "evidence": "어떤 문장과 키워드가 일치하는지 설명",
     *   "reason": "최종 판단 근거"
     * }
     */
    private EvaluationResult parseEvaluationResult(String rawResponse,
                                                   String requirementText,
                                                   Long requirementId) {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new IllegalStateException("OpenAI 응답이 비어 있습니다.");
        }

        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // ChatCompletion 구조에서 content 추출
            JsonNode contentNode = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            String content = contentNode.asText("");

            if (content.isBlank()) {
                throw new IllegalStateException("OpenAI 응답에서 content를 찾을 수 없습니다.");
            }

            // content 내부를 JSON으로 파싱한다고 가정
            JsonNode resultJson = objectMapper.readTree(content);

            String status = resultJson.path("status").asText("NOT_FULFILLED");
            double score = resultJson.path("score").asDouble(0.0);
            int matchedKeywordCount = resultJson.path("matchedKeywordCount").asInt(0);
            int totalKeywordCount = resultJson.path("totalKeywordCount").asInt(0);
            String evidence = resultJson.path("evidence").asText("");
            String reason = resultJson.path("reason").asText(evidence); // reason이 없으면 evidence로 대체

            return new EvaluationResult(
                    requirementId,          // 일단 여기서는 null 또는 evaluateAll에서 세팅한 값
                    requirementText,        // 현재 평가 중인 요구사항 원문
                    status,
                    score,
                    matchedKeywordCount,
                    totalKeywordCount,
                    evidence,
                    reason
            );

        } catch (IOException e) {
            throw new RuntimeException("OpenAI 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
