package com.scit.iLog.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scit.iLog.domain.AnalysisResult;
import com.scit.iLog.service.AnalysisResultService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisResultController {
    
	/*
	 * 2025.02.06
	 * anaysisResult 관련 Controller 코드 작성
	 * 수정자 : 김보경
	 */
	
    private final AnalysisResultService analysisResultService;
    
    // 특정 분석 결과 조회
    @GetMapping("/{analysisId}")
    public String getAnalysisResult(@PathVariable Long analysisId, Model model) {
        AnalysisResult result = analysisResultService.getAnalysisResult(analysisId);
        
        // 분석 결과가 없을 경우!
        if (result == null) {
            return "redirect:/analysis/analysisResults";
        }

        model.addAttribute("result", result);
        return "children/analysisResult";
    }
    
    
    
    // 분석 결과 목록 조회
    @GetMapping("/analysisResults")
    public String getAnalysisResults(Model model) {
        List<AnalysisResult> results = analysisResultService.getAllAnalysisResults();
        model.addAttribute("results", results);
        return "children/analysisResults";
    }
    
    
    
    
    // 분석 결과 삭제
    @PostMapping("/{analysisId}/delete")
    public String deleteAnalysisResult(@PathVariable Long analysisId, RedirectAttributes redirectAttributes) {
        try {
            analysisResultService.deleteAnalysisResult(analysisId);
            redirectAttributes.addFlashAttribute("message", "분석 결과가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "삭제 실패: " + e.getMessage());
        }

        return "redirect:/analysis/analysisResults"; 
    }
}
