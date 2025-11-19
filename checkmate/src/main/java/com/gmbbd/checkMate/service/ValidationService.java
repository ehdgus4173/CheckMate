package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ValidationService {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * (수정됨)
     * 업로드된 MultipartFile을 검증하기 위한 메서드.
     * 🔥 기존에는 MultipartFile을 임시 파일로 저장하고(File 기반 검증 재사용)
     *    File.tempPath → multipartFile.transferTo()로 인해
     *    원본 temp 파일이 삭제되어 이후 파싱 단계에서 FileNotFoundException이 발생했음.
     *
     * 🔥 해결:
     *    - 임시 파일 생성 로직 완전 제거
     *    - transferTo() 절대 금지
     *    - MultipartFile 자체의 정보로 검증 수행
     *
     * 검증 내용:
     *  - null / empty 체크
     *  - 파일 크기 제한
     *  - 확장자(pdf/docx/txt) 체크
     */
    public void validateFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ApiException("업로드된 파일이 비어있습니다.");
        }

        // 파일명 확보 (없다면 오류)
        String originalName = multipartFile.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new ApiException("파일 이름을 확인할 수 없습니다.");
        }

        // 용량 제한
        if (multipartFile.getSize() > MAX_SIZE) {
            throw new ApiException("파일 용량은 10MB 이하만 지원합니다.");
        }

        // 확장자 검사
        String lower = originalName.toLowerCase();
        if (!(lower.endsWith(".pdf") || lower.endsWith(".docx") || lower.endsWith(".txt"))) {
            throw new ApiException("지원하지 않는 파일 형식입니다. (pdf/docx/txt)");
        }
    }

    /**
     * (원본)
     * 파싱된 텍스트의 길이/내용을 검증하는 메서드.
     */
    public void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new ApiException("파싱된 텍스트가 비어 있습니다.");
        }
        if (text.length() < 20) {
            throw new ApiException("텍스트가 너무 짧아 분석이 불가능합니다.");
        }
    }
}
