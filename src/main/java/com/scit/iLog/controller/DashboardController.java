package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 사용자의 대시보드 페이지 조회에 필요한 요청을 처리하는 컨트롤러.
 * 대시보드는
 * 부모 권한을 가진 사용자가 조회하는 대시보드,
 * 선생님 권한을 가진 사용자가 조회하는 대시보드
 * 로 구분되어 있습니다.
 * 각 대시보드는 서로 다른 목적을 가지며,
 * 권한에 따라 조회할 수 있는 정보의 종류가 다릅니다.
 */
@Controller
public class DashboardController {
    /**
     * @return 부모 권한을 가진 사용자만 볼 수 있는 대시보드 페이지 템플릿의 경로를 반환합니다.
     */
    @GetMapping("/parentDashboard")
    public String handleGetParentDashboardView() {
        return "dashboard/parentView";
    }

    /**
     * @return 선생님 권한을 가진 사용자만 볼 수 있는 대시보드 페이지의 템플릿의 경로를 반환합니다.
     */
    @GetMapping("/teacherDashboard")
    public String handleGetTeacherDashboardView() {
    	return "dashboard/teacherView";
    }
}
