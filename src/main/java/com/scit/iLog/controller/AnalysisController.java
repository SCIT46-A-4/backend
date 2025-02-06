package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalysisController {
	
	@GetMapping("/children/analysis")
    public String analysis() {
        return "/children/analysis/insertView";
    }
}
