package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChildrenController {

	@GetMapping("/statisticsDetails")
    public String statisticsDetails() {
        return "/children/statisticsDetails";
    }


	@GetMapping("/children/diaryDetails")
	public String getDiaryDetails()
	{
		return "/children/diaryDetails";
	}


	// 아동 정보 상세 페이지로 이동
	@GetMapping("/children/infoDetails")
	public String infoDetails(){
		return "/children/infoDetails";
	}
}
