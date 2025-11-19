package com.gmbbd.checkMate.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message); // 에러 메시지 전달
    }
}
