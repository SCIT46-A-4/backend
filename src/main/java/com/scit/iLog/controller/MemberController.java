package com.scit.iLog.controller;

import com.scit.iLog.dto.MyPageDTO;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.RelationShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final RelationShipService relationShipService;

    @GetMapping("/{memberId}/info")
    public String handleGetMyPage(
            @PathVariable("memberId") Long memberId,
            Model model
    ) {
        model.addAttribute("member",memberService.findById(memberId));
        return "/member/detailsView";
    }

    @DeleteMapping("/{memberId}")
    public boolean handleDeleteMember(
            @PathVariable("memberId") Long memberId,
            @RequestParam("deleteAllChildren") boolean deleteAllChildren
    ) {
        if (deleteAllChildren) {
            relationShipService.deleteAllChildrenOf(memberId);
        }
        memberService.deleteMember(memberId);
        return true;
    }

    @GetMapping("/{memberId}/edit")
    public String handleGetMemberUpdateView(
            @PathVariable("memberId") Long memberId,
            Model model
    ) {
        MyPageDTO myPageDTO = memberService.getMyPageDataById(memberId);
        model.addAttribute("myPage", myPageDTO);
        return "/member/updateView";
    }
}
