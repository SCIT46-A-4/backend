package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scit.iLog.dto.MemberDTO;
import com.scit.iLog.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final MemberService memberService;

	/*
     * 회원가입 화면 요청
     * 2025-02-06 / 전제환 / 47
     */
    @GetMapping("/signUp")
	public String getJoinPage() {
		return "auth/signUpView";
	}

	/*
	 * 회원가입처리
	 * 2025-02-06 / 전제환 / 47
	 */
	@PostMapping("/signUp")
	public String idCheck(@ModelAttribute MemberDTO memberDTO) {
		memberService.join(memberDTO);

		return "redirect:/user/signIn";
	}

	/*
	 * 로그인 화면 요청
	 * 2025-02-06 / 전제환 / 47
	 */
	@GetMapping("/signIn")
	public String getLoginPage() {
		return "auth/signInView";
	}

	// ID/PW 찾기 페이지로 이동하기
    @GetMapping("/idPwFind")
    public String idPwFind() {
		return "auth/idPwFindView";
    }
}
