package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	
	// ID/PW 찾기 페이지로 이동하기
    @GetMapping("/inPwFind")
    public String inPwFind() {
        return "inPwFind";
    }
}
