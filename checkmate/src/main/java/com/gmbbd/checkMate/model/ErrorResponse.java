package com.gmbbd.checkMate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data                       // getter/setter, toString 등 자동 생성
@AllArgsConstructor         // 모든 필드를 인자로 받는 생성자 자동 생성
public class ErrorResponse {

    private String message; // 에러 메시지 본문 (클라이언트에게 전달될 내용)
}
