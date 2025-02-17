package com.scit.iLog.controller;

import com.scit.iLog.dto.ClaimDetailsDTO;
import com.scit.iLog.dto.claims.ClaimsAndAnswersDTO;
import com.scit.iLog.dto.claims.ClaimsInsertDTO;
import com.scit.iLog.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * 클레임(문의) 관련 요청을 처리하는 Spring MVC 컨트롤러입니다.  
 * 사용자가 문의 목록을 조회하거나, 새로운 문의를 작성할 수 있도록 합니다.
 * 이 컨트롤러는 다음의 페이지에서 필요한 요청을 처리합니다.
 * - 고객센터 관련 페이지 모음
 */
@Controller
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;

    /**
     * 문의 목록 페이지 요청
     * 
     * 사용자가 등록한 모든 문의(클레임)를 조회하여 목록 페이지(/claims/claimListView.html)로 이동합니다.
     * @param model - 조회된 문의 목록을 뷰로 전달하기 위한 모델 객체
     * @return "/claims/claimsListView" (문의 목록 페이지 경로)
     *
     * CL-1
     */
    @GetMapping({"", "/"})
    public String handleGetClaimsListView(Model model) {
        ClaimsAndAnswersDTO claimsAndAnswersDTO = claimService.getAllClaimsAndAnswers();
        model.addAttribute("claims", claimsAndAnswersDTO);
        return "claims/claimListView";
    }
    
    /**
     * 문의 등록 페이지 요청
     * 
     * 사용자가 새로운 문의를 작성할 수 있도록 문의 입력 폼 페이지(claims/claimInsertView.html)로 이동합니다.
     * @param - 빈 `ClaimsDTO` 객체를 추가하여 폼에 바인딩
     * @return "/claims/claimsInsertView" (문의 작성 페이지 경로)
     *
     * CL-2
     */
    @GetMapping("/new")
    public String handleGetClaimsInsertView() {
        return "claims/claimInsertView";
    }

    /**
     * 문의 등록 요청
     * 
     * 사용자가 입력한 문의 데이터를 저장한 후 문의 목록 페이지로 이동합니다.  
     * 만약 `memberId`가 유효하지 않으면 에러 메시지와 함께 문의 작성 페이지로 다시 이동합니다.
     * 
     * @param claimInsertDTO - 사용자가 입력한 문의 데이터
     * @param redirectAttributes - 성공 또는 실패 메시지를 전달하기 위한 객체
     * @return "redirect:/claims" (성공 시) 또는 "redirect:/claims/new" (실패 시)
     *
     * 로그인한 사용자 아이디는 이렇게 가져옵니다. - 호준
     *
     * CL-2
     */
    @PostMapping("/new")
    public String handleInsertClaim(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ClaimsInsertDTO claimInsertDTO,
            RedirectAttributes redirectAttributes
    ) {
        try {
            claimService.saveClaim(userDetails.getUsername(),claimInsertDTO);
            redirectAttributes.addFlashAttribute("successMessage", "클레임이 성공적으로 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "멤버 ID가 유효하지 않습니다.");
            return "redirect:/claims/new"; // 에러 발생 시 입력 폼으로 리디렉트
        }
        return "redirect:/claims";
    }

    /*
        CL-3(고객센터-단일 문의 조회 페이지, 아직 안만들었음)
     */
    @GetMapping("/claims/{claimId}")
    public String handleGetClaimDetailsView(
            @PathVariable("claimId") Long claimId,
            Model model
    ) {
        ClaimDetailsDTO claimDetailsDTO = claimService.getClaimDetailsById(claimId);
        model.addAttribute("claimDetails", claimDetailsDTO);
        return "claims/claimDetailsView";
    }

    /**
     * 문의 삭제
     * 25/2/11 은진
     * 사용자가 입력한 특정 문의를 삭제하고 싶을 때, 삭제 후 문의 목록으로 이동
     * @param claimId - 삭제할 문의 id
     * @return - 삭제 후 클레임 화면으로 리디렉트
     *
     * CL-3(고객센터-단일 문의 조회 페이지, 아직 안만들었음)
     */
    @DeleteMapping("/{claimId}")
    public String handleDeleteClaim(
            @PathVariable("claimId") Long claimId
    ) {
        claimService.deleteClaim(claimId);
        return "redirect:/claims";
    }

    /*
        CL-3(고객센터-단일 문의 조회 페이지, 아직 안만들었음) 혹은
        CL-4(별도의 답변 달기 페이지)
        - 페이지 조회 요청 처리 메서드
        - 답변 저장 요청 처리 메서드
        구현 필요
     */
}