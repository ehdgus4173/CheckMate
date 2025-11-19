package com.gmbbd.checkMate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 하나의 요구사항에 대한 평가 결과를 담는 모델 클래스.
 * - requirementId        : 어떤 요구사항의 결과인지 (Requirement.id)
 * - requirementText      : 요구사항 원문 텍스트
 * - status               : "FULFILLED" / "PARTIAL" / "NOT_FULFILLED"
 * - score                : 매칭 점수 (0.0 ~ 1.0)
 * - matchedKeywordCount  : 과제 텍스트에서 실제로 매칭된 키워드 개수
 * - totalKeywordCount    : 평가에 사용된 전체 키워드 개수
 * - evidence             : 상태 판단의 근거(키워드 매칭 설명 또는 LLM 분석 결과)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    /** 평가 대상이 된 요구사항 ID */
    private Long requirementId;

    /** 요구사항 원문 텍스트 */
    private String requirementText;

    /** 평가 상태: FULFILLED / PARTIAL / NOT_FULFILLED */
    private String status;

    /** 매칭 점수 (matchedKeywordCount / totalKeywordCount) */
    private double score;

    /** 과제 텍스트에서 실제로 매칭된 키워드 개수 */
    private int matchedKeywordCount;

    /** 평가에 사용된 전체 키워드 개수 */
    private int totalKeywordCount;

    /** 상태 판단의 근거 (키워드 매칭 기반 설명 or LLM 생성 근거) */
    private String evidence;

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
