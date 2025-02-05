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
	
	@GetMapping("/surveys")
	public String surveysPage() {
		return "children/surveys";
	}
}
