package com.scit.iLog.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 2025-02-17~20 이도훈  WrongSignInIdException 커스텀 예외 처리 클래스 생성
 */
public class WrongSignInIdException extends AuthenticationException {

    public WrongSignInIdException(String errorMessage) {
        super(errorMessage);
    }
}
