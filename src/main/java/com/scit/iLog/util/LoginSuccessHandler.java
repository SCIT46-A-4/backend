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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (!(authentication.getPrincipal() instanceof MemberDetails memberDetails)) {
            log.warn("로그인 성공했지만, 인증된 사용자 정보를 가져올 수 없습니다.");
            response.sendRedirect("/auth/signIn?error=unauthorized");
            return;
        }

        // 1️⃣ targetUrl 파라미터 확인
        String targetUrlParam = request.getParameter("targetUrl");
        if (targetUrlParam != null && !targetUrlParam.isBlank()) {
            requestCache.removeRequest(request, response);

            // targetUrl이 "/auth/permissionTeacher" 인 경우, token과 requestId 등 추가 파라미터 보존
            if ("/auth/permissionTeacher".equals(targetUrlParam)) {
                String token = request.getParameter("token");
//                String requestId = request.getParameter("requestId");
                StringBuilder redirectUrl = new StringBuilder(targetUrlParam);
                redirectUrl.append("?");
//                boolean first = true;
                if (token != null) {
                    redirectUrl.append("token=").append(token);
//                    first = false;
                }
//                if (requestId != null) {
//                    if (!first) {
//                        redirectUrl.append("&");
//                    }
//                    redirectUrl.append("requestId=").append(requestId);
//                    first = false;
//                }
                // success 파라미터 추가
//                if (!first) {
//                    redirectUrl.append("&");
//                }
//                redirectUrl.append("success=true");

                log.info("targetUrl 파라미터 발견: {} - 해당 URL로 리다이렉트", redirectUrl.toString());
                response.sendRedirect(redirectUrl.toString());
                return;
            } else {
                // 다른 targetUrl인 경우에도 success=true를 추가하여 리다이렉트
                String redirectUrl = targetUrlParam + (targetUrlParam.contains("?") ? "&" : "?") + "success=true";
                log.info("targetUrl 파라미터 발견: {} - 해당 URL로 리다이렉트", redirectUrl);
                response.sendRedirect(redirectUrl);
                return;
            }
        }

        // 2️⃣ 저장된 요청이 있는 경우 처리 (예: 인증이 필요한 페이지로의 접근 시)
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null && !savedRequest.getRedirectUrl().contains("favicon")) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("저장된 요청 발견: {}", targetUrl);
            response.sendRedirect(targetUrl);
            return;
        }

        // 3️⃣ 기본 리다이렉트 경로 설정 (역할에 따라)
        String defaultRedirect = (memberDetails.getRelationType() == RelationType.GUARDIAN)
                ? "/dashboard/guardian"
                : "/dashboard/teacher";
        response.sendRedirect(defaultRedirect);
    }
}