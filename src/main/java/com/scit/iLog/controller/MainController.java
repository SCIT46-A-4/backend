package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
    @GetMapping({"","/"})
    public String handleIndex() {
        return "index";
    }
    
    @GetMapping("/guides")
    public String userGuide() {
        return "guides";
    }
    
    
}
