package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AuthController 
{
	@GetMapping("/user/login")
	public String getLoginPage()
	{
		return "/auth/signin";
	}
}
