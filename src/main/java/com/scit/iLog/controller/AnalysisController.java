package com.scit.iLog.controller;

import com.scit.iLog.domain.AnalysisResult;
import com.scit.iLog.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/children/analysis")
public class AnalysisController {
    private final AnalysisResultService analysisResultService;
	
    // 그림,사진,음성 분석 페이지 요청
	@GetMapping
    public String analysis() {
        return "/children/analysis/insertView";
    }

    // 25/2/6 은진 : '분석 결과 페이지' 요청
    @GetMapping("/result")
    public String analysisResult() {
        return "/children/analysisResult";
    }

    // 25/2/6 은진 : '분석결과 목록 페이지' 요청
    @GetMapping("/results")
    public String analysisResults() {
        return "/children/analysisResults";
    }

    // 특정 분석 결과 조회
    @GetMapping("/{analysisId}")
    public String getAnalysisResult(
            @PathVariable Long analysisId,
            Model model
    ) {
        AnalysisResult result = analysisResultService.getAnalysisResult(analysisId);

        // 분석 결과가 없을 경우!
        if (result == null) {
            return "redirect:/analysis/analysisResults";
        }

        model.addAttribute("result", result);
        return "children/analysis/analysisResultView";
    }

    // 분석 결과 목록 조회
    @GetMapping("/analysisResults")
    public String getAnalysisResults(Model model) {
        List<AnalysisResult> results = analysisResultService.getAllAnalysisResults();
        model.addAttribute("results", results);
        return "children/analysis/analysisResultListView";
    }

    // 분석 결과 삭제
    @PostMapping("/{analysisId}/delete")
    public String deleteAnalysisResult(
            @PathVariable Long analysisId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            analysisResultService.deleteAnalysisResult(analysisId);
            redirectAttributes.addFlashAttribute("message", "분석 결과가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "삭제 실패: " + e.getMessage());
        }

        return "redirect:/analysis/analysisResults";
    }
}
