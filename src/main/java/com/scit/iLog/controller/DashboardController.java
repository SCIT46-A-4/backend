package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    

    @GetMapping("/parentDashboard")
    public String parentDashboard() {
    	
        return "children/parentDashboard";
    }

    @GetMapping("/teacherDashboard")
    public String teacherDashboard() {

    	return "children/teacherDashboard";
    }

    // 내 아이 상세 페이지로 이동
    @GetMapping("/children/details")
    public String details(){
        return "/children/details";
    }
}
