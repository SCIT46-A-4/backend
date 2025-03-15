package com.scit.iLog.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.scit.iLog.exception.WrongSignInIdException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 2025-02-17~20 이도훈 LoginFailureHandler클래스 생성
 */
@Component
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
            ) throws IOException, ServletException {
    	
    	String errorMessage = "로그인에 실패했습니다. 관리자에게 문의하세요.";
    	
    
    	 // 1️⃣ InternalAuthenticationServiceException`이면 내부 원인을 확인
    	/* InternalAuthenticationServiceException이란?
    	 * Spring Security에서 인증 과정 중 내부 서버 오류가 발생하면 던지는 예외
    	 * 보통 DB에서 사용자 정보를 조회하는 과정에서 오류가 발생했을 때 발생
    	 * InternalAuthenticationServiceException 자체는 Spring Security 내부 예외이고,
    	 * 그 원인 (cause)이 WrongSignInIdException일 때만 아이디 오류로 판단
    	 */
        if (exception instanceof InternalAuthenticationServiceException 
            && exception.getCause() instanceof WrongSignInIdException)
        {
            errorMessage = "아이디가 틀렸습니다.";
        }
        // 비밀번호 오류 감지 (`BadCredentialsException` 사용)
        /*
         *BadCredentialsException이란?
         *Spring Security에서 입력한 비밀번호가 DB에 저장된 비밀번호와 다를 때 발생하는 예외
         *올바른 ID가 입력되었지만, 비밀번호가 틀린 경우 발생
         *즉, BadCredentialsException은 아이디는 올바르게 입력되었지만 비밀번호가 틀렸을 때 발생하는 예외
         */
        else if (exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 틀렸습니다.";
        }
        
        log.warn("❌ 로그인 실패 : {}", errorMessage);

        // 한글 인코딩 문제 방지
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8"); 

        // 로그인 페이지로 실패 메시지를 전달하며 이동
        this.setDefaultFailureUrl("/auth/signIn?error=true&errMessage=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}
