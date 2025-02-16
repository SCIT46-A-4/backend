package com.scit.iLog.controller;

import com.scit.iLog.dto.auth.SignUpDTO;
import com.scit.iLog.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 사용자 인증 관련 요청을 처리하는 컨트롤러 클래스
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final MemberService memberService;

	/**
	 * 회원가입 화면 요청
	 * 로그인 화면(/auth/signInView.html)에서 GetMapping을 통해
	 * 회원가입 페이지(/auth/signUpView.html)로 이동하도록 처리하는 메서드
	 *
	 * @return auth/signUpView (회원가입 페이지 경로)
	 */
	@GetMapping("/signUp")
	public String handleGetSignUpView() {
		return "auth/signUpView";
	}

	/*
		A-2
	 */
	/**
	 * 회원가입 처리
	 * 사용자가 회원가입 폼(/auth/signUpView.html)에 정보를 입력하고 제출하면
	 * PostMapping으로 해당 데이터(MemberDTO)를 받아 회원가입을 처리하는 메서드
	 * 회원가입이 완료되면 로그인 페이지(/auth/signInView.html)로 리다이렉트
	 *
	 * @param signUpDTO
	 * @return /auth/signInView (로그인 페이지 경로)
	 */
	@PostMapping("/signUp")
	public String handleSignUp(@ModelAttribute SignUpDTO signUpDTO) {
		memberService.join(signUpDTO);
		return "redirect:/auth/signInView";
	}

	/*
		A-1
	 */
	/**
	 * 로그인 화면 요청
	 * 시작 화면(index.html)에서 GetMapping을 통해
	 * 로그인 페이지(/auth/signInView.html)에 이동하도록 처리하는 메서드
	 *
	 * @return auth/signInView (로그인 페이지 경로)
	 */
	@GetMapping("/signIn")
	public String handleGetSignInView() {
		return "auth/signInView";
	}

	/*
		A-3
	 */
	/**
	 * ID, PW 찾기 페이지 화면 요청
	 * 로그인 화면(/auth/signInView.html)에서 GetMapping을 통해
	 * 아이디/비밀번호 찾기 화면(/auth/idPwFindView.html)로 이동하도록 처리하는 메서드
	 *
	 * @return auth/idPwFindView (ID/PW 찾기 페이지 경로)
	 */
	@GetMapping("/idPwFind")
	public String handleGetIdPwFindView() {
		return "auth/idPwFindView";
	}

	/*
		A-2
	 */
	@GetMapping("/checkSignInIdExists")
	public boolean handleCheckSignInIdExists(
			@RequestParam("signInId") String signInId
	) {
		return memberService.checkSignInIdExists(signInId);
	}
}
