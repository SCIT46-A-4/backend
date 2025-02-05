package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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


	// 아동 정보 상세 페이지로 이동
	@GetMapping("/children/infoDetails")
	public String infoDetails(){
		return "/children/infoDetails";
	}
}
