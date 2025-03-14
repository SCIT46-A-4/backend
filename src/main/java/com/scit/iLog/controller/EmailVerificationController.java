package com.scit.iLog.controller;

import com.scit.iLog.dto.member.MemberDetailsDTO;
import com.scit.iLog.service.EmailService;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.child.ChildService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailService emailService;
    private final MemberService memberService;
    private final ChildService childService;

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
            response.put("message", "인증 코드가 전송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생", e);
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

    @GetMapping("/checkDuplicatedEmail")
    public boolean handleGetCheckDuplicatedEmail(
            @RequestParam("email") String email
    ) {
        return emailService.checkDuplicatedEmail(email);
    }


@PostMapping("/send-invite-link")
@ResponseBody
public boolean sendEmailInviteLink(
        @RequestParam(name = "childId") Long childId,
        @RequestParam(name = "memberId") Long memberId,
        @RequestParam(name = "alias") String alias,
        @RequestParam(name = "inviteeEmail") String inviteeEmail)
{
        MemberDetailsDTO memberDto = memberService.getMemberDetailsById(memberId);
        childService.findBasicInfoById(childId);

        emailService.sendAuthInviteEmail(
                memberDto.email(),
                memberDto.name(),
                childId,
                memberId,
                alias,
                inviteeEmail,
                "" );
        return true;
}

/**
 * 2025-03-10~13 이도훈
 * @param permissionId
 * @param childId
 * @param alias
 * @return
 * @throws Exception
 */
@DeleteMapping("/cancel-invite-link/{permissionId}/{childId}/{alias}")
@ResponseBody
public boolean cancelEmailInviteLink(
        @PathVariable(name = "permissionId") Long permissionId,
        @PathVariable(name = "childId") Long childId,
        @PathVariable(name = "alias") String alias) throws Exception
{
    try {

        // 서비스에서 취소 요청을 처리하는 메서드 호출
        boolean result = emailService.cancelEmailInviteLink(permissionId, childId,  alias);

        if (!result) {
            return false; // 취소 실패 시 false 반환
        }

        return true; // 취소 성공 시 true 반환

    } catch (Exception e) {
        throw new Exception("취소 처리 도중 문제가 발생했습니다.");
    }
}

@GetMapping("/verifyLink")
public ResponseEntity<String> verifyEmail(@RequestParam(name = "token") String token) throws Exception
{
    log.info("받은 token: " + token);
    emailService.findInviteCodeAndUpdate(token);

    return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
}
}