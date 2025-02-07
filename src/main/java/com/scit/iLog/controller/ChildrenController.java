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
/**
 * 아동(정보) 관련 기능을 처리하는 컨트롤러 클래스.
 * 
 * - 아동 상세 정보 조회 및 화면 반환
 * - 아동 감정 분석 통계 페이지 제공
 * - 아동 일기 상세 페이지 제공
 * - 아동 개요 정보 페이지 제공
 */
public class ChildrenController {
	
	/* 2025-02-06 ChildService 선언 */
	/**
	 * 컨트롤러에서 서비스를 사용할 준비를 하는 코드
	 * RequiredArgsConstructor를 통해 자동으로 생성자가 생성
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

	
	
	/**
     * "내 아이 한눈에 보기" 페이지를 보여주는 뷰를 반환하는 핸들러
     * @return children/childOverviewView.html 뷰 페이지
     */
	@GetMapping("/details")
	public String handleGetChildOverviewView() {
		return "/children/childOverviewView";
	}



	/**
     * 특정 ID에 해당하는 아동 상세 정보를 조회하여 "아동 정보 상세 페이지" 뷰에 전달하는 핸들러
     * @param id
     * @param model
     * @return children/childDetailsView.html 뷰 페이지
     * 
     * 서비스 레이어에서 ChildDetailsDto를 가져와 뷰에 전달
     */
	@GetMapping("/infoDetails")
	public String handleGetChildDetailsView(@RequestParam(name="id") Long id, Model model) {
		// ID에 해당하는 아동 상세 정보를 조회
		ChildDetailsDto childDetailsDto = childService.seletInfoDetails(id);
		
		// 조회한 데이터를 뷰(Model)에 추가
		model.addAttribute("childDetailsDto", childDetailsDto);
		
		return "/children/childDetailsView";
	}
	

	
}
