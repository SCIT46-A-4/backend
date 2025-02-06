package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scit.iLog.dto.child.ChildInfoDetailsDto;
import com.scit.iLog.service.InfoDetailsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/children")
public class ChildrenController {
	/* 2025-02-06 ChildService 선언 */
	private final InfoDetailsService childService;

	@GetMapping("/statisticsDetails")
    public String statisticsDetails() {
        return "/children/statistics/detailsView";
    }

	// 25/2/5 일기 상세페이지
	@GetMapping("/diaryDetails")
	public String getDiaryDetails() {
		return "/children/diaryDetailsView";
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
		ChildInfoDetailsDto childInfoDetailsDto = childService.seletInfoDetails(id);
		
		model.addAttribute("childInfoDetailsDto", childInfoDetailsDto);
		return "/children/childDetailsView";
	}
}
