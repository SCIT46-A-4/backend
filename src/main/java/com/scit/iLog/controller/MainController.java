package com.scit.iLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/*
	이 컨트롤러는 다음의 페이지에서 필요한 요청을 처리합니다.
	- 랜딩페이지
 */
@Controller
@RequiredArgsConstructor
public class MainController {
	/**
	 * "기본/메인 페이지"를 보여주는 뷰를 반환하는 핸들러
	 *
	 * @return index.html 뷰 페이지
	 * <p>
	 * L-1
	 */
	@GetMapping({"", "/"})
	public String handleGetIndex(
			@RequestParam(value = "lang", required = false) String lang
	) {
		if (StringUtils.hasText(lang) && lang.equals("en")) {
			return "index-en";
		}
		if (StringUtils.hasText(lang) && lang.equals("jp")) {
			return "index-jp";
		}
		return "index";
	}

	@GetMapping("/terms")
	public String handleGetTerms() {
		return "terms";
	}

	@GetMapping("/privacy")
	public String handleGetPrivacy() {
		return "/privacy";
	}

	@GetMapping("/youth-policy")
	public String handleGetYouthPolicy() {
		return "/youth-policy";
	}
}
