package com.scit.iLog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    /**
     * 인증번호를 포함한 이메일을 전송합니다.
     * @param to 수신자 이메일 주소
     * @param code 인증번호
     */
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("안녕하세요,\n\n귀하의 인증 코드는: " + code + "\n\n감사합니다.");
        mailSender.send(message);
    }

    public static String generateVerificationCode() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int code = 100000 + current.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 2025-03-11~12
     * 
     * ✅ 아이디 찾기 이메일 전송  
     * 사용자가 입력한 이메일 주소로 해당 계정의 아이디를 전송합니다.
     * 
     * @param to - 이메일을 받을 사용자 주소
     * @param id - 사용자의 아이디 (회원가입 시 등록한 로그인 아이디)
     */
    public void sendIdFindEmail(String to, String id) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("안녕하세요,\n\n귀하의 인증 코드는: " + id + "\n\n감사합니다.");
        mailSender.send(message);
    }

    /**
     * 2025-03-11~12
     * 
     * ✅ 임시 비밀번호 전송  
     * 사용자의 요청에 따라 새로운 임시 비밀번호를 생성하여 이메일로 전송합니다.
     * 
     * @param to - 이메일을 받을 사용자 주소
     * @param newPwd - 새롭게 발급된 임시 비밀번호 (사용자는 로그인 후 변경 필요)
     */
    public void sendPwdFindEmail(String to, String newPwd) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ilog@ilog.com");  // 발신자 주소
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("안녕하세요,\n\n귀하의 비밀번호는: " + newPwd + "\n\n감사합니다.");
        mailSender.send(message);
    }
}

