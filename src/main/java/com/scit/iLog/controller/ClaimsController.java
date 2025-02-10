package com.scit.iLog.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scit.iLog.dto.ClaimsDTO;
import com.scit.iLog.service.ClaimsService;

import lombok.RequiredArgsConstructor;

/**
 * 클레임(문의) 관련 요청을 처리하는 Spring MVC 컨트롤러입니다.  
 * 사용자가 문의 목록을 조회하거나, 새로운 문의를 작성할 수 있도록 합니다.
 */
@Controller
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimsController {
    private final ClaimsService claimsService;

    /**
     * 문의 목록 페이지 요청
     * 
     * 사용자가 등록한 모든 문의(클레임)를 조회하여 목록 페이지(/claims/claimsListView.html)로 이동합니다.
     * @param model - 조회된 문의 목록을 뷰로 전달하기 위한 모델 객체
     * @return "/claims/claimsListView" (문의 목록 페이지 경로)
     */
    @GetMapping({"", "/"})
    public String handleGetClaimsListView(Model model) {
        List<ClaimsDTO> claimsList = claimsService.getAllClaims();
        model.addAttribute("inquiries", claimsList);
        return "/claims/claimsListView";
    }
    
    /**
     * 문의 등록 페이지 요청
     * 
     * 사용자가 새로운 문의를 작성할 수 있도록 문의 입력 폼 페이지(/claims/claimsInsertView.html)로 이동합니다.
     * @param model - 빈 `ClaimsDTO` 객체를 추가하여 폼에 바인딩
     * @return "/claims/claimsInsertView" (문의 작성 페이지 경로)
     */
    @GetMapping("/new")
    public String handleGetClaimsInsertView(Model model) {
        model.addAttribute("claimsDTO", new ClaimsDTO());
        return "/claims/claimsInsertView";
    }

    /**
     * 문의 등록 요청
     * 
     * 사용자가 입력한 문의 데이터를 저장한 후 문의 목록 페이지로 이동합니다.  
     * 만약 `memberId`가 유효하지 않으면 에러 메시지와 함께 문의 작성 페이지로 다시 이동합니다.
     * 
     * @param claimsDTO - 사용자가 입력한 문의 데이터
     * @param redirectAttributes - 성공 또는 실패 메시지를 전달하기 위한 객체
     * @return "redirect:/claims" (성공 시) 또는 "redirect:/claims/new" (실패 시)
     */
    @PostMapping("/new")
    public String handleInsertClaim(@ModelAttribute ClaimsDTO claimsDTO, RedirectAttributes redirectAttributes) {
        try {
            claimsService.saveClaim(claimsDTO);
            redirectAttributes.addFlashAttribute("successMessage", "클레임이 성공적으로 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "멤버 ID가 유효하지 않습니다.");
            return "redirect:/claims/new"; // 에러 발생 시 입력 폼으로 리디렉트
        }
        return "redirect:/claims";
    }
}