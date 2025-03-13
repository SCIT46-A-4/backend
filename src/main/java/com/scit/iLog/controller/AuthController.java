package com.scit.iLog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scit.iLog.dto.auth.IdFindDTO;
import com.scit.iLog.dto.auth.PwFindDTO;
import com.scit.iLog.dto.auth.SignUpDTO;
import com.scit.iLog.service.EmailService;
import com.scit.iLog.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 사용자 인증 관련 요청을 처리하는 컨트롤러 클래스
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final MemberService memberService;
	private final EmailService emailService;

	/**
	 * 회원가입 화면 요청
	 * 로그인 화면(/auth/signInView.html)에서 GetMapping을 통해
	 * 회원가입 페이지(/auth/signUpView.html)로 이동하도록 처리하는 메서드
	 *
	 * @return auth/signUpView (회원가입 페이지 경로)
	 */
	@GetMapping("/signUp")
	public String handleGetSignUpView() {
		log.info("handleGetSignUpView");
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
		return "redirect:/auth/signIn";
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

	/**
	 * A-2 2025-02-17~20 이도훈
	 * 아이디 중복 확인 요청 처리
	 * memberService.checkSignInIdExists(signInId) 를 호출하여 DB에 동일한 ID가 존재하는지 확인
	 * 존재하면 true, 없으면 false 를 ResponseEntity<Boolean> 으로 반환
	 * @param signInId
	 * @return
	 */
	@GetMapping("/checkSignInIdExists")
	public ResponseEntity<Boolean> handleCheckSignInIdExists(
			@RequestParam("signInId") String signInId
	) {
		boolean isExists = memberService.checkSignInIdExists(signInId);
        return ResponseEntity.ok(isExists);  // ✅ 중복이면 true, 사용 가능하면 false 반환
	}
	
    /**
     * A-3 2025-03-11~12 전제환
     * 
     * ✅ 아이디 찾기 기능  
     * 사용자가 입력한 이메일을 통해 가입된 아이디를 찾고, 해당 이메일로 아이디 정보를 전송합니다.
     * 
     * @param email - 사용자가 입력한 이메일
     * @return IdFindDTO (성공 여부 반환)
     */
	@ResponseBody
	@GetMapping("/idFind")
	public IdFindDTO handleGetIdFind(@RequestParam("email") String email) {
		
			// 이메일을 통해 가입된 아이디 찾기
	        String signInId = memberService.getSignInIdByEmail(email);
	        
	        // 찾은 아이디를 이메일로 전송
	        emailService.sendIdFindEmail(email, signInId);

	        return new IdFindDTO(true);
	}

    /**
     * A-3 2025-03-11~12 전제환 
     * 
     * ✅ 비밀번호 찾기 기능  
     * 사용자가 입력한 아이디와 이메일이 일치하는지 확인한 후, 임시 비밀번호를 발급하여 이메일로 전송합니다.
     * 
     * @param email - 사용자가 입력한 이메일
     * @param signInId - 사용자가 입력한 아이디
     * @return PwFindDTO (성공 여부 및 메시지 반환)
     */
	@ResponseBody
	@GetMapping("/pwFind")
	public PwFindDTO handleGetPwFind(@RequestParam("email") String email, @RequestParam("signInId") String signInId) {
		
		// 아이디와 이메일이 일치하는지 확인
		boolean isValidEmail = memberService.checkMemberEmail(email, signInId);
		if (isValidEmail) {
			// 임시 비밀번호 발급
			String newPwd = memberService.resetPassword(signInId);
			// 새 비밀번호를 이메일로 전송
			emailService.sendPwdFindEmail(email, newPwd);
			
			return new PwFindDTO(true, "임시 비밀번호가 성공적으로 발송되었습니다.");
		}
		return new PwFindDTO(false, "유효하지 않은 이메일입니다.");
	}
}
