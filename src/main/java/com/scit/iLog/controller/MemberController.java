package com.scit.iLog.controller;

import com.scit.iLog.dto.member.MemberUpdateDTO;
import com.scit.iLog.dto.member.MemberUpdateRequestDTO;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.child.RelationShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.scit.iLog.config.SecurityConfig.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final RelationShipService relationShipService;

    /*
        M-1
        스프링 시큐리티 컨텍스트를 이용하므로 사용자 아이디를 노출하지 않는 것이
        더 편리하고 보안성도 높은 것 같아 변경합니다.
        기존: /members/{memberId}/info
        변경후: /members/myDetails
     */
    @GetMapping("/myDetails")
    public String handleGetMyDetails(
            Authentication authentication,
            Model model
    ) {
        if (!authentication.isAuthenticated()) return "redirect:/";
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        model.addAttribute("memberDetails",memberService.getMemberDetailsById(memberDetails.getId()));
        return "/member/memberDetailsView";
    }

    /*
        M-1
     */
    @DeleteMapping("/quit")
    public boolean handleDeleteMember(
            Authentication authentication,
            @PathVariable("memberId") Long memberId,
            @RequestParam("deleteAllChildren") boolean deleteAllChildren
    ) {
        if (!authentication.isAuthenticated()) return false;
        if (deleteAllChildren) {
            relationShipService.deleteAllRelationShipOf(memberId);
        }
        memberService.deleteMember(memberId);
        return true;
    }

    /*
        M-2(내정보 수정 페이지, 아직 디자인 안됨)
     */
    @GetMapping("/edit")
    public String handleGetMemberUpdateView(
            Authentication authentication,
            Model model
    ) {
        if (!authentication.isAuthenticated()) return "redirect:/";
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        MemberUpdateDTO memberUpdateDTO = memberService.getMemberUpdateDataById(memberDetails.getId());
        model.addAttribute("memberDetails", memberUpdateDTO);
        return "/member/memberUpdateView";
    }

    /*
        M-2(내정보 수정 페이지, 아직 디자인 안됨)
     */
    @PostMapping("/edit")
    public String updateMember(
            @ModelAttribute
            @Validated
            MemberUpdateRequestDTO memberUpdateRequestDTO,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (!authentication.isAuthenticated()) return "redirect:/";
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        if (bindingResult.hasErrors()) {
            return "/member/memberUpdateView";
        }
        memberService.updateMember(
                memberDetails.getId(),
                memberUpdateRequestDTO.email(),
                memberUpdateRequestDTO.userPwd());
        return "redirect:/members/myDetails";
    }
}

