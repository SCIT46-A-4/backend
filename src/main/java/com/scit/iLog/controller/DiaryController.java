package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class DiaryController {

	// 우리 아이 일기 통계로 보기 로 이동하기! = 우리 아이 일기장 버튼
    @GetMapping("/diaryStatistics")
    public String diaryStatistics() {
        return "/children/diaryStatistics";
    }

    //일기장 목록 페이지 요청
    @GetMapping("/diaries")
    public String selectAll(){
        return "/children/diaries";
    }
}
