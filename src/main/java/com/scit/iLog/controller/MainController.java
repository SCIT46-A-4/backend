package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;


/*
	이 컨트롤러는 다음의 페이지에서 필요한 요청을 처리합니다.
	- 랜딩페이지
 */
@Controller
@RequiredArgsConstructor
public class MainController {
	/**
	 * "기본/메인 페이지"를 보여주는 뷰를 반환하는 핸들러
	 * @return index.html 뷰 페이지
	 */
    @GetMapping({"","/"})
    public String handleIndex() {
		return "index";
    }
}
