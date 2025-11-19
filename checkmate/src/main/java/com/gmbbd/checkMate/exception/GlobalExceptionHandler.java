package com.gmbbd.checkMate.exception;

import com.gmbbd.checkMate.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice  // 전역 예외처리 핸들러
public class GlobalExceptionHandler {

    /**
     * ApiException이 발생했을 때 실행 (400)
     * ErrorResponse로 반환
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)         // HTTP 400 상태 반환
                .body(new ErrorResponse(e.getMessage())); // 예외 메시지 포함한 에러 응답 생성
    }

    /**
     * 그 외의 모든 예외
     * 500 INTERNAL_SERVER_ERROR로 통일
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {

        e.printStackTrace(); // 서버 콘솔에 전체 스택 트레이스 출력 (디버깅용)

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)        // HTTP 500 상태 반환
                .body(new ErrorResponse("Internal server error")); // 사용자에게는 일반화된 메시지만
    }
}
