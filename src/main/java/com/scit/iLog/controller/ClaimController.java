package com.scit.iLog.controller;

import com.scit.iLog.dto.claims.ClaimAnswerDTO;
import com.scit.iLog.dto.claims.ClaimDetailsDTO;
import com.scit.iLog.dto.claims.ClaimListViewDTO;
import com.scit.iLog.dto.claims.ClaimsInsertDTO;
import com.scit.iLog.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 2024-03-05 ~ 2024-03-09 전제환
 * 
 * **클레임(문의) 관련 요청을 처리하는 Spring MVC 컨트롤러입니다.**
 * 사용자가 문의 목록을 조회하거나, 새로운 문의를 작성할 수 있도록 합니다.
 * 이 컨트롤러는 다음의 페이지에서 필요한 요청을 처리합니다.
 * - 고객센터 관련 페이지 모음
 */
@Controller
@RequestMapping("/claims")
@RequiredArgsConstructor
@Slf4j
public class ClaimController {
	private final ClaimService claimService;

	/**
	 * **2024-03-09 전제환**
	 * 
	 * ✅ 문의 메인 페이지를 반환하는 메서드  
	 * @return "claims/claimMainPageView" (문의 메인 페이지)
	 * 
	 * CL-4
	 */
	@GetMapping({ "", "/" })
	public String handleGetClaimMainPageView() {
		return "claims/claimMainPageView";
	}

	/**
	 * **2024-03-05 전제환**
	 * 
	 * ✅ 문의 목록 페이지 요청  
	 * 사용자가 등록한 모든 문의(클레임)를 조회하여 목록 페이지로 이동합니다.  
	 * ADMIN 계정일 경우 모든 사용자의 문의를 조회할 수 있습니다.
	 * 
	 * @param userDetails - 현재 로그인한 사용자 정보
	 * @param pageable - 페이지네이션 정보
	 * @param model - 조회된 문의 목록을 뷰로 전달하기 위한 모델 객체
	 * @return "/claims/claimsListView" (문의 목록 페이지 경로)
	 *
	 * CL-1
	 */
	@GetMapping("/claimsList")
	public String handleGetClaimsListView(
			@AuthenticationPrincipal UserDetails userDetails,
			@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			Model model
	) {
		String signInId = userDetails.getUsername();

		// ✅ 본인이 작성한 글 + ADMIN은 모든 글 조회 가능
		Page<ClaimListViewDTO> claimsPage = claimService.getPagedUserClaims(signInId, pageable);
		
		int totalPages = claimsPage.getTotalPages();
		
	    // ✅ 글이 없거나 한 페이지도 안 차도 1페이지로 표시
	    if (totalPages == 0) {
	        totalPages = 1;
	    }

		model.addAttribute("claimsPage", claimsPage);
		model.addAttribute("currentPage", claimsPage.getNumber());
		model.addAttribute("totalPages", claimsPage.getTotalPages());

		return "claims/claimListView";
	}

	/**
	 * **2024-03-06 전제환**
	 * 
	 * ✅ 문의 등록 페이지 요청  
	 * 사용자가 새로운 문의를 작성할 수 있도록 문의 입력 폼 페이지로 이동합니다.
	 * 
	 * @param model - 빈 `ClaimsInsertDTO` 객체를 추가하여 폼에 바인딩
	 * @return "/claims/claimsInsertView" (문의 작성 페이지 경로)
	 *
	 * CL-2
	 */
	@GetMapping("/new")
	public String handleGetClaimsInsertView(Model model) {
		model.addAttribute("claimsDTO", new ClaimsInsertDTO()); // 빈 DTO 추가
		return "claims/claimInsertView";
	}

	/**
	 * **2024-03-06 전제환**
	 * 
	 * ✅ 문의 등록 요청  
	 * 사용자가 입력한 문의 데이터를 저장한 후 문의 목록 페이지로 이동합니다.  
	 * ADMIN 계정도 문의 등록이 가능합니다.
	 * 
	 * @param userDetails - 현재 로그인한 사용자 정보
	 * @param claimInsertDTO - 사용자가 입력한 문의 데이터
	 * @param redirectAttributes - 성공 또는 실패 메시지를 전달하기 위한 객체
	 * @return "redirect:/claimsList" (성공 시) 또는 "redirect:/claims/new" (실패 시)
	 *
	 * CL-2
	 */
	@PostMapping("/new")
	public String handleInsertClaim(
			@AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute ClaimsInsertDTO claimInsertDTO, RedirectAttributes redirectAttributes
	) {
		log.info("📌 handleInsertClaim 호출됨 - 사용자 ID: {}", userDetails.getUsername());
		log.info("✅ 입력된 제목: {}, 내용: {}, 유형: {}", claimInsertDTO.getTitle(), claimInsertDTO.getContent(),
				claimInsertDTO.getType());

		try {
			// 로그인한 사용자 ID 가져오기
			String signInId = userDetails.getUsername();

			// 문의 저장
			claimService.saveClaim(signInId, claimInsertDTO);

			redirectAttributes.addFlashAttribute("successMessage", "문의가 성공적으로 등록되었습니다.");
			return "redirect:/claims/claimsList"; // 성공 시 리스트 페이지로 이동

		} catch (IllegalArgumentException e) {
			log.error("🚨 문의 등록 중 오류 발생: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "문의 등록 중 오류 발생: " + e.getMessage());
			return "redirect:/claims/new"; // 실패 시 다시 등록 페이지로 이동
		}
	}

	/**
	 * **2024-03-07 전제환**
	 * 
	 * ✅ 특정 문의 상세 조회 요청  
	 * 
	 * @param claimId - 조회할 문의 ID
	 * @param userDetails - 로그인한 사용자 정보
	 * @param model - 뷰로 전달할 모델 객체
	 * @return "claims/claimDetailsView" (문의 상세 페이지)
	 * 
	 * CL-3
	 */
	@GetMapping("/{claimId}")
	public String handleGetClaimDetailsView(
			@PathVariable("claimId") Long claimId,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
	) {
		ClaimDetailsDTO claimDetailsDTO = claimService.getClaimDetailsById(claimId);

		model.addAttribute("claimDetails", claimDetailsDTO);
		return "claims/claimDetailsView";
	}

	/**
	 * **2024-03-08 전제환**
	 * 
	 * ✅ 문의 삭제 요청  
	 * 사용자가 작성한 특정 문의를 삭제합니다.
	 * 
	 * @param claimId - 삭제할 문의 ID
	 * @return ResponseEntity - 삭제 성공 또는 실패 메시지
	 * 
	 * CL-3
	 */
	@DeleteMapping("/{claimId}")
	public ResponseEntity<?> handleDeleteClaim(@PathVariable("claimId") Long claimId) {
		log.info("📌 삭제 요청 받은 claimId: {}", claimId);
		try {
			claimService.deleteClaim(claimId);
			return ResponseEntity.ok().body("{\"message\": \"삭제 성공\"}"); // ✅ JSON 응답 반환
		} catch (IllegalArgumentException e) {
			log.error("문의 삭제 중 오류 발생: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"삭제 실패: " + e.getMessage() + "\"}"); // ✅ 오류 메시지 반환
		}
	}

	/**
	 * **2024-03-09 전제환**
	 * 
	 * ✅ 문의에 대한 답변 작성 (관리자 전용)  
	 * 관리자 계정만 답변을 작성할 수 있습니다.
	 * 
	 * @param claimId - 답변을 달 문의 ID
	 * @param userDetails - 로그인한 관리자 정보
	 * @param answerDTO - 답변 내용 DTO
	 * @param redirectAttributes - 성공 또는 실패 메시지를 전달하기 위한 객체
	 * @return "redirect:/claims/{claimId}" (문의 상세 페이지로 이동)
	 */
	@PostMapping("/{claimId}/answer")
	public String handlePostClaimAnswer(
			@PathVariable("claimId") Long claimId,
			@AuthenticationPrincipal UserDetails userDetails,
			@ModelAttribute ClaimAnswerDTO answerDTO,
			RedirectAttributes redirectAttributes
	) {
		String signInId = userDetails.getUsername();

		log.info("📌 [컨트롤러] 답변 등록 요청 - claimId: {}, 작성자: {}, 내용: {}", claimId, signInId, answerDTO.content());

		try {
			claimService.saveClaimAnswer(signInId, claimId, answerDTO);
			redirectAttributes.addFlashAttribute("successMessage", "답변이 성공적으로 등록되었습니다.");
			log.info("✅ [컨트롤러] 답변 등록 성공 - claimId: {}", claimId);
		} catch (IllegalArgumentException e) {
			log.error("🚨 [컨트롤러] 답변 등록 실패: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/claims/" + claimId; // 문의 상세 페이지로 이동
	}
}