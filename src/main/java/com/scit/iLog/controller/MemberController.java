package com.scit.iLog.controller;

import com.scit.iLog.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{memberId}/info")
    public String handleGetMyPage(
            @PathVariable("memberId") Long memberId,
            Model model
    ) {
        model.addAttribute("member",memberService.findById(memberId));
        return "/member/detailsView";
    }
}
