package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalysisController {
	
    // 그림,사진,음성 분석 페이지 요청
	@GetMapping("/children/analysis")
    public String analysis() {
    	
        return "/children/analysis";
    }

    // 25/2/6 은진 : '분석 결과 페이지' 요청
    @GetMapping("/children/analysisResult")
    public String analysisResult(){
        return "/children/analysisResult";
    }

    // 25/2/6 은진 : '분석결과 목록 페이지' 요청
    @GetMapping("/children/analysisResults")
    public String analysisResults(){
        return "/children/analysisResults";
    }
}
