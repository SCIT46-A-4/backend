package com.scit.iLog.controller;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.dto.member.MemberUpdateDTO;
import com.scit.iLog.dto.member.MemberUpdateRequestDTO;
import com.scit.iLog.repository.PermissionRequestRepository;
import com.scit.iLog.service.ClaimService;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.analysis.AnalysisService;
import com.scit.iLog.service.child.ChildDiaryService;
import com.scit.iLog.service.child.ChildRecordService;
import com.scit.iLog.service.child.RelationShipService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.scit.iLog.config.SecurityConfig.MemberDetails;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final RelationShipService relationShipService;
    private final ClaimService claimService;
    private final PermissionRequestRepository permissionRequestRepository;
    private final ChildDiaryService childDiaryService;
    private final AnalysisService analysisService;
    private final ChildRecordService childRecordService;

    /*
        M-1
        스프링 시큐리티 컨텍스트를 이용하므로 사용자 아이디를 노출하지 않는 것이
        더 편리하고 보안성도 높은 것 같아 변경합니다.
        기존: /members/{memberId}/info
        변경후: /members/myDetails
     */
    @GetMapping("/myDetails")
    public String handleGetMyPage(
            Authentication authentication,
            Model model
    ) {
        if (!authentication.isAuthenticated()) return "redirect:/";

        try {
            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            model.addAttribute("memberDetails", memberService.getMemberDetailsById(memberDetails.getId()));
        } catch (Exception e) {
            log.error("회원 정보 조회 중 오류 발생: {}", e.getMessage());
            model.addAttribute("errorMessage", "회원 정보를 불러오는 중 문제가 발생했습니다.");
        }

        return "/member/memberDetailsView";
    }


    /*
        M-1
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quit/v1")
    public boolean handleDeleteMember(
            HttpServletRequest request,
            Authentication authentication,
            @RequestParam(value = "deleteAllChildren", required = false) boolean deleteAllChildren
    ) {
        if (!authentication.isAuthenticated()) return false;
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        memberService.inValidateMember(memberDetails.getId());
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return true;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/quit/v2")
    public boolean handleDeleteMemberV2(
            HttpServletRequest request,
            Authentication authentication
    ) {
        if (!authentication.isAuthenticated()) return false;
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        if (memberDetails.getRelationType() == RelationType.GUARDIAN) {
            claimService.deleteClaimsAndAnswersOf(memberDetails.getId());
            relationShipService.deleteAllChildrenOf(memberDetails.getId());
        } else if (memberDetails.getRelationType() == RelationType.TEACHER) {
            memberService.deleteMemberWithRelationShips(memberDetails.getId());
            claimService.deleteClaimsAndAnswersOf(memberDetails.getId());
        }

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
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
        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
        return "/member/memberUpdateView";
    }

    @ResponseBody
    @PostMapping("/password/validate")
    public boolean handlePostPasswordReset(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam("password") String password
    ) {
        return memberService.isDuplicatedPassword(memberDetails.getId(), password);
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

