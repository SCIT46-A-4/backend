package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DiaryController {
	
	// 우리 아이 일기 통계로 보기 로 이동하기! = 우리 아이 일기장 버튼
    @GetMapping("/children/diaryStatistics")
    public String diaryStatistics() {
        return "/children/diaryStatistics";
    }
	
	// 일기 목록 자세히 보기 로 이동하기!
    @GetMapping("/children/diaries")
    public String diaries() {
        return "/children/diaries";
    }
    
    
}
