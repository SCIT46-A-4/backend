package com.scit.iLog.controller;

import com.scit.iLog.service.EmailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailService emailService;

    /**
     * 인증번호 전송 API
     */
    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, Object>> handleSendVerificationCode(
            @RequestParam("email") String email,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        // 인증번호 생성
        String code = emailService.generateVerificationCode();
        // 생성된 인증번호를 세션에 저장 (실제 서비스에서는 Redis 등 캐시나 DB에 저장할 수도 있음)
        session.setAttribute("verificationCode", code);
        session.setAttribute("verificationEmail", email);
        log.info("code: {}, email: {}", code, email);
        try {
            emailService.sendVerificationEmail(email, code);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "인증번호 전송에 실패했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 인증번호 검증 API
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> handleVerifyCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        String storedCode = (String) session.getAttribute("verificationCode");
        String storedEmail = (String) session.getAttribute("verificationEmail");

        if (!ObjectUtils.isEmpty(storedCode) &&
                !ObjectUtils.isEmpty(storedEmail) &&
                storedEmail.equals(email) &&
                storedCode.equals(code)
        ) {
            response.put("success", true);
            // 인증 성공 시 세션에서 인증번호 정보를 제거하거나, 인증된 상태로 처리
            session.removeAttribute("verificationCode");
            session.removeAttribute("verificationEmail");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "인증 번호가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

