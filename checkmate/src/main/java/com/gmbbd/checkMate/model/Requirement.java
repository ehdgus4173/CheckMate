package com.gmbbd.checkMate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requirement 모델
 *  - id: 요구사항 번호
 *  - rawText: 원본 요구사항 문장
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Requirement {

    private Long id;        // 요구사항 번호
    private String rawText; // 요구사항 내용
}