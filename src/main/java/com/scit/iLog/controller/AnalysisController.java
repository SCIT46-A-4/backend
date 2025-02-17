package com.scit.iLog.controller;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import com.scit.iLog.service.analysis.AnalysisResultService;
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

    // 분석 결과 목록 조회 | 분석결과 리스트를 조회해서 페이지 반환하는 메서드
    /*
        AI-1: (1)이냐(2)냐 보기의 전환은 자바스크립트로 처리합니다.
     */
    @GetMapping("/resultList")
    public String handleGetAnalysisResultListView(Model model)
    {
        // 모든 결과를 조회하고 데이터를 담는다 분석 결과 리스트 페이지 반환
        List<AnalysisResultEntity> results = analysisResultService.getAllAnalysisResults();
        model.addAttribute("results", results);
        return "children/analysis/analysisResultListView";
    }

    // 그림,사진,음성 분석 페이지 요청
    /*
        AI-2
     */
	@GetMapping("/insertView")
    public String handleGetAnalysisInsertView()
	{	
    	// 25/2/7 준성 : 우리 아이 그림, 사진, 음성 분석 페이지 반환
        return "/children/analysis/analysisInsertView";
    }

    /*
        AI-2
        @TODO 그림, 사진 분석 POST 요청을 처리하는 메서드가 필요합니다.
     */

    // 특정 분석 결과 조회(select)
    // 25/2/7 준성: 메서드 이름 변경 getAnalysisResult -> getAnalysisResultView
    /*
        AI-3
     */
    @GetMapping("/results/{analysisResultId}")
    public String handleGetAnalysisResultView(
            @PathVariable("analysisResultId") Long analysisResultId,
            Model model
    ) {
        AnalysisResultEntity result = analysisResultService.getAnalysisResult(analysisResultId);

        // 분석 결과가 없을 경우!
        /*
            @TODO 리팩토링 필요! 위험하고 가독성 나쁜 코드
            null 사용하지 않도록 해야합니다. 서비스 레벨에서 에러 던지는게 좋습니다.
         */
        if (result == null) 
        {
        	// 25/2/7 준성: GetMapping(/resultList) (분석결과 목록 페이지)로 리다이렉트
            return "redirect:/children/analysis/resultList";
        }

        // 분석 결과가 있다면 데이터 담아서 analysisResultView 페이지 반환
        model.addAttribute("result", result);
        return "children/analysis/analysisResultView";
    }

    // 분석 결과 삭제
    @PostMapping("/results/{analysisResultId}/delete")
    public String handleDeleteAnalysisResult(
            @PathVariable("analysisResultId") Long analysisResultId,
            RedirectAttributes redirectAttributes
    ) 
    {
        /*
            @TODO 여기는 아주 잘해주셨는데 리팩토링이 필요하네요.
            응답을 ResponseBody로하고 리다이렉트를 프론트엔드에서 jquery로 처리해야합니다.
         */
        try {
        	// 서비스에 삭제 요청
            analysisResultService.deleteAnalysisResult(analysisResultId);
            redirectAttributes.addFlashAttribute("message", "분석 결과가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "삭제 실패: " + e.getMessage());
        }

        // 25/2/7 준성: resultList url 반환(분석결과 목록페이지)
        return "redirect:/children/analysis/resultList";
    }
}
