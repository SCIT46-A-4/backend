package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/children")
public class HealthCheckController
	{
	    // 25/2/10 준성: api-42 영유아 건강 문진 결과 목록 페이지 요청
	    @GetMapping("/healthCheckList")
	    public String getServayResultList(@RequestParam(name = "offset") Long offset,
	    								  @RequestParam(name = "limit") int limit)
	    {
	    	
	    	
	    	return "/children/healthCheck/healthCheckListView";
	    }
	    
	    // 25/2/10 준성: api-43 영유아 건강 문진 결과 삭제 요청
	    @DeleteMapping("/healthCheck/{healthCheckId}")
	    public String deleteHealthCheckId(@PathVariable(name = "healthCheckId") Long healthCheckId,
	    								  RedirectAttributes rtrt)
	    {
	    	rtrt.addAttribute("offset", 0);
	    	rtrt.addAttribute("limit", 10);
	    	return "redirect:/children/healthCheckList";
	    }
	}
