package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scit.iLog.dto.child.ChildInfoDetailsDto;
import com.scit.iLog.service.InfoDetailsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChildrenController {

	@GetMapping("/children/statisticsDetails")
    public String statisticsDetails() {
        return "/children/statisticsDetails";
    }


	// 25/2/5 일기 상세페이지
	@GetMapping("/children/diaryDetails")
	public String getDiaryDetails() {
		return "/children/diaryDetails";
	}

	// 25/2/5 내 아이 상세페이지
	@GetMapping("/children/details")
	public String getChildrenDetails() {
		return "/children/details";
	}

	@GetMapping("/surveys")
	public String surveysPage() {
		return "children/surveys";
	}


	/* 2026-02-06 이도훈 삭제 예정
	 * // 아동 정보 상세 페이지로 이동
	 * 
	 * @GetMapping("/children/infoDetails") public String infoDetails(){ return
	 * "/children/infoDetails"; }
	 */
	
	/* 2025-02-06 이곳부터 이도훈 작업 부분 */
	
	/* 2025-02-06 ChildService 선언 */
	private final InfoDetailsService childService;
	
	/* infoDetails에 아동 정보 조회 */
	@GetMapping("/children/infoDetails")
	public String infoDetails(@RequestParam(name="id") Long id, Model model) {
		ChildInfoDetailsDto childInfoDetailsDto = childService.seletInfoDetails(id);
		
		model.addAttribute("childInfoDetailsDto", childInfoDetailsDto);
		return "/children/infoDetails";
	}
}
