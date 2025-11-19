package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * CompareService
 *  - 요구사항 텍스트와 과제 텍스트를 키워드 단위로 단순 비교해서
 *    FULFILLED / PARTIAL / NOT_FULFILLED 상태를 판정하는 서비스.
 *  - 현재 버전은 단위 테스트(CompareServiceTest)에 맞춰
 *    점수/상태 규칙을 맞춘 구현이다.
 */
@Service
public class CompareService {

    // 이 비율 이상 매칭되면 "충분히 충족(FULFILLED)"으로 본다.
    // (예: 키워드 3개 중 2개 이상 매칭 → 2/3 ≈ 0.66 ≥ 0.6 → FULFILLED)
    private static final double FULFILLED_THRESHOLD = 0.6;

    /**
     * (기존 기능)
     * 요구사항 목록과 과제 텍스트를 받아서
     * 각 요구사항별 EvaluationResult 리스트를 반환한다.
     */
    public List<EvaluationResult> evaluateByKeywordMatch(List<Requirement> requirements,
                                                         String assignmentText) {

        List<EvaluationResult> results = new ArrayList<>();

        if (requirements == null || requirements.isEmpty()) {
            return results;
        }

        if (assignmentText == null) {
            assignmentText = "";
        }
        String normalizedAssignment = normalize(assignmentText);

        for (Requirement req : requirements) {
            if (req == null) continue;

            EvaluationResult r = compareRequirementInternal(req, normalizedAssignment);
            results.add(r);
        }

        return results;
    }

    /**
     * (신규) AnalysisService용 단일 비교 메서드
     *  - 하나의 Requirement와 제출 텍스트를 입력받아
     *    EvaluationResult 1개를 반환한다.
     *  - 기존 evaluateByKeywordMatch()와 동일한 규칙/주석/로직을 유지한다.
     */
    public EvaluationResult compare(Requirement req, String submissionText) {

        if (req == null) {
            return null;
        }

        if (submissionText == null) {
            submissionText = "";
        }

        String normalizedAssignment = normalize(submissionText);

        return compareRequirementInternal(req, normalizedAssignment);
    }

    /**
     * 내부 공통 로직
     *  - 단일 Requirement에 대해 키워드 기반 비교를 수행하고
     *    EvaluationResult를 생성하는 공통 메서드.
     */
    private EvaluationResult compareRequirementInternal(Requirement req, String normalizedAssignment) {

        String normalizedReq = normalize(req.getRawText());

        // 1. 요구사항 문장을 토큰(단어) 단위로 쪼갠다.
        String[] tokens = normalizedReq.split("\\s+");

        int totalKeywords = 0;   // 실제로 의미 있는 키워드 수
        int matched = 0;         // 과제 텍스트에서 발견된 키워드 수

        for (String token : tokens) {
            if (token.isBlank()) continue;
            if (isStopWord(token)) continue; // 조사/접속사 등은 제외

            totalKeywords++;

            if (normalizedAssignment.contains(token)) {
                matched++;
            }
        }

        double score;
        String status;

        // 키워드가 하나도 없으면 → 점수 0, NOT_FULFILLED
        if (totalKeywords == 0) {
            status = "NOT_FULFILLED";
            score = 0.0;

        } else {
            double ratio = (double) matched / totalKeywords;

            if (ratio >= FULFILLED_THRESHOLD) {
                // 충분히 매칭되면 FULFILLED
                status = "FULFILLED";
                // 테스트 요구사항에 맞게, FULFILLED일 때는 점수를 1.0으로 고정
                score = 1.0;

            } else if (matched > 0) {
                // 일부만 매칭되면 PARTIAL
                status = "PARTIAL";
                // PARTIAL일 때는 실제 비율을 점수로 사용
                score = ratio;

            } else {
                // 하나도 안 맞으면 NOT_FULFILLED
                status = "NOT_FULFILLED";
                score = 0.0;
            }
        }

        // 결과 객체 생성 및 값 세팅
        EvaluationResult r = new EvaluationResult();
        r.setRequirementId(req.getId());
        r.setStatus(status);
        r.setScore(score);
        r.setMatchedKeywordCount(matched);
        r.setTotalKeywordCount(totalKeywords);

        return r;
    }

    /**
     * 비교를 위해 텍스트를 소문자 + 특수문자 제거 + 공백 정리 형태로 변환.
     */
    private String normalize(String text) {
        if (text == null) return "";
        return text
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", " ") // 한글/영어/숫자/공백만 남김
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * 의미 없는 조사/접속사 등을 stop word로 정의.
     * (필요하면 나중에 더 추가 가능)
     */
    private boolean isStopWord(String token) {
        return List.of(
                "을", "를", "은", "는", "이", "가",
                "에", "에서", "및", "그리고", "또는",
                "으로", "으로서", "으로써", "와", "과"
        ).contains(token);
    }
}
