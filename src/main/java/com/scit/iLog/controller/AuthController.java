package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.scit.iLog.dto.MemberDTO;
import com.scit.iLog.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	
	private final MemberService memberService;

	// ID/PW 찾기 페이지로 이동하기
    @GetMapping("/auth/idPwFind")
    public String idPwFind() {
        return "idPwFind";
    }
    
    /*
     * 로그인 화면 요청
     * 2025-02-06 / 전제환 / 47
     */
	@GetMapping("/user/login")
	public String getLoginPage()
	{
		return "/auth/signin";
	}
    
	/*
     * 회원가입 화면 요청
     * 2025-02-06 / 전제환 / 47
     */
    @GetMapping("/user/join")
	public String getJoinPage()
	{
		return "/auth/signup";
	}
    
	/*
	 * 회원가입처리
	 * 2025-02-06 / 전제환 / 47
	 */
	@PostMapping("/user/joinProcess")
	public String idCheck(@ModelAttribute MemberDTO memberDTO) {
		
		boolean result = memberService.joinProc(memberDTO);
		log.info("회원가입성공: {}",result);
		
		
		return "redirect:/user/login";
	}
}
