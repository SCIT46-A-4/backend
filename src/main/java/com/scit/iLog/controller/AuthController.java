package com.scit.iLog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.permition.PermissionRequestDTO;
import com.scit.iLog.domain.permition.PermissionRequestStatus;
import com.scit.iLog.dto.auth.PermissionTeacherDTO;
import com.scit.iLog.dto.auth.SignUpDTO;
import com.scit.iLog.service.EmailService;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.child.ChildService;

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
	private final ChildService childService;
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
	
	@GetMapping("/parentsView")
	public String getAuthPage()
	{
		
		return "/children/permissions/guardianView";
		
	}
	
	@GetMapping("/permissionGuardian/{memberId}/{childId}")
	public String handleGetPermissionGuardian(
			@PathVariable(name="memberId") Long memberId,
			@PathVariable(name="childId") Long childId,
			Model model) throws Exception 
	{
		// memberId를 이용해서 사용자가 요청한 repository 조회
		MemberEntity member = memberService.findById(memberId).orElseThrow(() -> new Exception("멤버가 존재하지 않습니다"));
		
		ChildEntity child = childService.findById(childId).orElseThrow(() -> new Exception("멤버가 존재하지 않습니다"));
		
		List<PermissionRequestDTO> list = emailService.findPermissionRequestDTOList(memberId);
		
	    // 송신중
	    long pendingCount = list.stream()
	                            .filter(dto -> dto.getPermissionRequestStatus() == PermissionRequestStatus.PENDING)
	                            .count();
	    
	    // 승인 완료
	    long acceptedCount = list.stream()
	                             .filter(dto -> dto.getPermissionRequestStatus() == PermissionRequestStatus.ACCEPTED)
	                             .count();
	    
		
		model.addAttribute("child", child);
		model.addAttribute("member", member);
		model.addAttribute("list", list);
	    model.addAttribute("pendingCount", pendingCount);
	    model.addAttribute("acceptedCount", acceptedCount);

		return "children/permissions/guardianView";
	}
	
	
	@GetMapping("/permissionTeacher/{memberId}")
	public String handleGetPermissionTeacher(@PathVariable(name = "memberId") Long memberId, Model model) throws Exception 
	{
		List<PermissionTeacherDTO> waitRequestList = new ArrayList<>();
		List<PermissionTeacherDTO> permissionList = new ArrayList<>();
		
		emailService.findAllByPermissionEntity(memberId, waitRequestList, permissionList);
		model.addAttribute("permissionList", permissionList);
		model.addAttribute("waitRequestList", waitRequestList);
		
		return "children/permissions/teacherView";
	}

	@DeleteMapping("/permissionTeacher/delete/{entityId}")
	@ResponseBody
	public boolean deletePermissionTable(@PathVariable(name = "entityId") Long id)
	{
		boolean result = emailService.deletePermissionTable(id);

		if(!result) return false;
		
		return true;
	}
	
}
