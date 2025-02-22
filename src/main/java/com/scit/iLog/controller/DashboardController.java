package com.scit.iLog.controller;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.dto.dashboard.ParentDashboardChildListDTO;
import com.scit.iLog.service.MemberService;
import com.scit.iLog.service.child.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.scit.iLog.config.SecurityConfig.MemberDetails;

/**
 * 사용자의 대시보드 페이지 조회에 필요한 요청을 처리하는 컨트롤러.
 * 대시보드는
 * 부모 권한을 가진 사용자가 조회하는 대시보드,
 * 선생님 권한을 가진 사용자가 조회하는 대시보드
 * 로 구분되어 있습니다.
 * 각 대시보드는 서로 다른 목적을 가지며,
 * 권한에 따라 조회할 수 있는 정보의 종류가 다릅니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
    private final ChildService childService;
    /*
        D-1
     */
    @GetMapping("/guardian")
    public String handleGetParentDashboardView(
            @AuthenticationPrincipal MemberDetails memberDetails,
            Model model
            ) {
        ParentDashboardChildListDTO childList = childService.getChildrenProfilesOf(memberDetails.getId());

        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().toString());
        model.addAttribute("childList", childList);
        return "dashboard/dashboardGuardianView";
    }

    /**
     * D-2
     * @return 선생님 권한을 가진 사용자만 볼 수 있는 대시보드 페이지의 템플릿의 경로를 반환합니다.
     * 이 페이지에서는 하나의 페이지에서 여러 정보를 필요로 하므로
     * 관련된 API의 구현이 필요합니다.
     */
    @GetMapping("/teacher")
    public String handleGetTeacherDashboardView() {
    	return "dashboard/dashboardTeacherView";
    }
}
