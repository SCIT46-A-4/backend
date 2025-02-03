package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

	// ID/PW 찾기 페이지로 이동하기
    @GetMapping("/auth/idPwFind")
    public String idPwFind() {
        return "idPwFind";
    }

	@GetMapping("/user/login")
	public String getLoginPage()
	{
		return "/auth/signin";
	}
}
