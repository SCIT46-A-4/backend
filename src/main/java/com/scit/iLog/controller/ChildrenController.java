package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scit.iLog.dto.child.ChildDetailsDto;
import com.scit.iLog.service.InfoDetailsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/children")
public class ChildrenController {
	
	/* 2025-02-06 ChildService 선언 */
	/*
	 * 컨트롤러에서 서비스를 사용할 준비를 하는 코드
	 * @RequiredArgsConstructor를 통해 자동으로 생성자가 생성
	 * InfoDetailsService 클래스 주입, 실제 사용할 객체 이름 childService
	 */
	private final InfoDetailsService childService;

	
	
	/**
	 * "우리 아이 일기 감정 분석 그래프" 페이지를 보여주는 뷰를 반환하는 핸들러
	 * @return children/statistics/detailsView.html 뷰 페이지
	 */
	@GetMapping("/statisticsDetails")
    public String handleGetStatisticsDetailsView() {
        return "/children/statistics/detailsView";
    }

	
	
	/**
     * "일기 상세" 페이지를 보여주는 뷰를 반환하는 핸들러
     * @return children/diaries/diaryDetailsView.html 뷰 페이지
     */
	@GetMapping("/diaryDetails")
	public String handleGetDiaryDetailsView() {
		return "/children/diaries/diaryDetailsView";
	}

	
	
	
	// 25/2/5 내 아이 상세페이지
	@GetMapping("/details")
	public String getChildrenDetails() {
		return "/children/childOverviewView";
	}

	/* 2026-02-06 이도훈 삭제 예정
	 * // 아동 정보 상세 페이지로 이동
	 * 
	 * @GetMapping("/children/infoDetails") public String infoDetails(){ return
	 * "/children/infoDetails"; }
	 */

	
	/* infoDetails에 아동 정보 조회 */
	@GetMapping("/infoDetails")
	public String infoDetails(@RequestParam(name="id") Long id, Model model) {
		ChildDetailsDto childDetailsDto = childService.seletInfoDetails(id);
		
		model.addAttribute("childDetailsDto", childDetailsDto);
		return "/children/childDetailsView";
	}
}
