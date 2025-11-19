package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;

public interface LlmService {

    /**
     * Evaluate a single requirement using LLM.
     *
     * @param requirementText 요구사항 한 줄
     * @param documentText    과제 전체 텍스트
     */
    EvaluationResult evaluateRequirement(String requirementText, String documentText);


}
