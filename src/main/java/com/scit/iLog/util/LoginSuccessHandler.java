package com.scit.iLog.util;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.domain.RelationType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 2025-02-17~20 이도훈 LoginSuccessHandler클래스 생성
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	// 특정 URL요청으로 접속시 해당 URL저장
	private final CustomRequestCache requestCache = new CustomRequestCache();
	
    @Override
    public void onAuthenticationSuccess(
    		HttpServletRequest request,
    		HttpServletResponse response,
            Authentication authentication
            ) throws IOException {
    	
    	
        /* Principal이 MemberDetails 타입인지 검증
         * authentication.getPrincipal()은 로그인한 사용자의 인증 정보를 반환합니다.
         * 일반적으로 UserDetails를 구현한 객체(예: MemberDetails)가 반환됩니다.
         * instanceof를 사용하여 authentication.getPrincipal()이 MemberDetails 타입인지 검사합니다
         * authentication.getPrincipal()이 MemberDetails가 아니면,
         * memberDetails 변수에 할당되지 않고 if 문을 실행합니다.
         * */

        if (!(authentication.getPrincipal() instanceof MemberDetails memberDetails)) {
            log.warn("로그인 성공했지만, 인증된 사용자 정보를 가져올 수 없습니다.");
            response.sendRedirect("/auth/signIn?error=unauthorized");
            return;
        }

        // 1️⃣ targetUrl 파라미터 확인
        String targetUrlParam = request.getParameter("targetUrl");
        if (targetUrlParam != null && !targetUrlParam.isBlank()) {
            requestCache.removeRequest(request, response);
            log.info("targetUrl 파라미터 발견: {} - 해당 URL로 리다이렉트", targetUrlParam);
            
            // 🔥 targetUrl에 success=true 추가
            response.sendRedirect(targetUrlParam + "?success=true");
            return;
        }

        // 2️⃣ 기존 저장된 요청이 있는 경우 처리
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("저장된 요청 발견: {}", targetUrl);
            response.sendRedirect(targetUrl);
            return;
        }

        // 3️⃣ 기본 리다이렉트 경로 설정
        String redirectUrl = (memberDetails.getRelationType() == RelationType.GUARDIAN)
                ? "/dashboard/guardian"
                : "/dashboard/teacher";

        response.sendRedirect(redirectUrl);
    }
}