package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsController {
	
	@GetMapping("/customerCenter")
	public String customerCenter() {
        return "/claims";
    }
}
