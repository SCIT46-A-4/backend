package com.scit.iLog.config;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@WebFilter(urlPatterns = "/verifyLink")
public class PermissionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 쿼리 파라미터 추출
        String token = req.getParameter("token");
        String targetUrl = req.getParameter("targetUrl");
        String requestId = req.getParameter("requestId");

        // (URL 인코딩 필요시 추가)
        String queryParams = String.format("token=%s&targetUrl=%s", token, targetUrl);

        // Spring Security를 사용하여 인증 여부 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        if (!loggedIn) {
            // 로그인이 되어 있지 않으면 로그인 페이지로 리다이렉트 (쿼리 파라미터 보존)
            res.sendRedirect(req.getContextPath() + "/auth/signIn?" + queryParams);
        } else {
            // 로그인이 되어 있으면 targetUrl로 리다이렉트 (쿼리 파라미터 보존)
            res.sendRedirect(req.getContextPath() + "/auth/permissionTeacher");
        }
    }
}
