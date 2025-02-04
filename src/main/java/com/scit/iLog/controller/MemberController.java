package com.scit.iLog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    // 회원가입 페이지 요청
    @GetMapping("/join")
    public String join(){
        return "/member/join";
    }

    @GetMapping("/{memberId}/info")
    public String handleGetMyPage(
            @PathVariable("memberId") Long memberId
    ) {
        return "/member/myinfo";
    }

}
