package com.gmbbd.checkMate.exception;

/**
 * 서비스 로직에서 명시적으로 발생시키는 커스텀 예외 클래스.
 * - RuntimeException을 상속하므로 체크 예외가 아님(throws 불필요)
 * - 에러 메시지를 전달해 GlobalExceptionHandler에서 처리하도록 함
 */
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message); // 부모(RuntimeException)에 에러 메시지 전달
    }
}
