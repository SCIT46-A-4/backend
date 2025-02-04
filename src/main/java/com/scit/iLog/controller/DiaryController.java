package com.scit.iLog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/children")
@Slf4j
public class DiaryController {

    //일기장 목록 페이지 요청
    @GetMapping("/diaries")
    public String selectAll(){
        return "/children/diaries";
    }
}
