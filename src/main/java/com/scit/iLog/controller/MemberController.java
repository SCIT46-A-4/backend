package com.scit.iLog.controller;

import com.scit.iLog.dto.member.MyPageDTO;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.child.RelationShipService;
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

    /*
        M-1
     */
    @GetMapping("/{memberId}/info")
    public String handleGetMyPage(
            @PathVariable("memberId") Long memberId,
            Model model
    ) {
        model.addAttribute("member",memberService.findById(memberId));
        return "/member/memberDetailsView";
    }

    /*
        M-1
        @TODO 등록된 아동 모두 삭제 옵션에 대한 기능 구현 필요.
     */
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

    /*
        M-2(내정보 수정 페이지, 아직 디자인 안됨)
     */
    @GetMapping("/{memberId}/edit")
    public String handleGetMemberUpdateView(
            @PathVariable("memberId") Long memberId,
            Model model
    ) {
        MyPageDTO myPageDTO = memberService.getMyPageDataById(memberId);
        model.addAttribute("myPage", myPageDTO);
        return "/member/memberUpdateView";
    }

    /*
        M-2(내정보 수정 페이지, 아직 디자인 안됨)
     */
}
