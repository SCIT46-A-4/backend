package com.scit.iLog.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;

@Component
public class CustomRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        // 저장하지 않을 URL 패턴을 필터링
        if (uri.startsWith("/static/") || uri.startsWith("/js/") ||
                uri.startsWith("/css/") || uri.startsWith("/images/")) {
            return; // 해당 요청은 저장하지 않음
        }
        super.saveRequest(request, response);
    }
}