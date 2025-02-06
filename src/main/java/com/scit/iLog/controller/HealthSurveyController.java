package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthSurveyController {
	
	// 설문쓰기_선택 바로가기
	@GetMapping("/children/surveyInsert")
	public String getsurveyInsert()
	{
		return "/children/survey/mental/insertView";
	}
		
	// 설문쓰기_신체 바로가기
	@GetMapping("/children/surveyBody")
	public String getsurveyBody()
	{
		return "/children/survey/health/insertView";
	}

	// 설문쓰기_심리 바로가기
	@GetMapping("/children/surveyMind")
	public String getsurveyMind()
	{
		return "/children/survey/surveyMind";
	}
}
