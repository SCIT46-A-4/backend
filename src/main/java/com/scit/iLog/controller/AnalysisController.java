package com.scit.iLog.controller;

import com.scit.iLog.domain.child.AnalysisResultEntity;
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
	@GetMapping("/insertView")
    public String getAnalysisInsertView() 
	{	
    	// 25/2/7 준성 : 우리 아이 그림, 사진, 음성 분석 페이지 반환
        return "/children/analysis/insertView";
    }

    // 25/2/6 은진 : '분석 결과 페이지' 요청
    @GetMapping("/result")
    public String getAnalysisResultView() 
    {
    	// 25/2/7 준성: 우리 아이 그림, 사진, 음성 분석 결과 페이지 반환
        return "/children/analysis/analysisResultView";
    }

    // 25/2/6 은진 : '분석결과 목록 페이지' 요청
    // 25/2/7 준성 : getMapping("/resultList")로 변경
    @GetMapping("/resultList")
    public String getAnalysisResultListView() 
    {
    	// 25/2/7 준성: 분석결과 목록 페이지 반환
        return "/children/analysis/analysisResultListView";
    }

    // 특정 분석 결과 조회(select)
    // 25/2/7 준성: 메서드 이름 변경 getAnalysisResult -> getAnalysisResultView 
    @GetMapping("/{analysisId}")
    public String getAnalysisResultView(
            @PathVariable Long analysisId,
            Model model
    ) {
        AnalysisResultEntity result = analysisResultService.getAnalysisResult(analysisId);

        // 분석 결과가 없을 경우!
        if (result == null) 
        {
        	// 25/2/7 준성: GetMapping(/resultList) (분석결과 목록 페이지)로 리다이렉트
            return "redirect:/children/analysis/resultList";
        }

        // 분석 결과가 있다면 데이터 담아서 analysisResultView 페이지 반환
        model.addAttribute("result", result);
        return "children/analysis/analysisResultView";
    }

    // 분석 결과 목록 조회 | 분석결과 리스트를 조회해서 페이지 반환하는 메서드
    // 25/2/7 준성: 임시로 메서드 이름 2로 변경(윗 메서드 이름과 중복)
    @GetMapping("/analysisResultList")
    public String getAnalysisResultListView2(Model model)
    {
    	// 모든 결과를 조회하고 데이터를 담는다 분석 결과 리스트 페이지 반환
        List<AnalysisResultEntity> results = analysisResultService.getAllAnalysisResults();
        model.addAttribute("results", results);
        return "children/analysis/analysisResultListView";
    }

    // 분석 결과 삭제
    @PostMapping("/{analysisId}/delete")
    public String deleteAnalysisResult(
            @PathVariable Long analysisId,
            RedirectAttributes redirectAttributes
    ) 
    {
        try {
        	// 서비스에 삭제 요청
            analysisResultService.deleteAnalysisResult(analysisId);
            redirectAttributes.addFlashAttribute("message", "분석 결과가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "삭제 실패: " + e.getMessage());
        }

        // 25/2/7 준성: resultList url 반환(분석결과 목록페이지)
        return "redirect:/children/analysis/resultList";
    }
}
