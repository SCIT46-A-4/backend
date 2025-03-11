package com.scit.iLog.controller;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.dto.child.ChildBasicInfoDTO;
import com.scit.iLog.dto.dashboard.ParentDashboardChildListDTO;
import com.scit.iLog.service.child.ChildService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * D-1,D-2
     * @return 부모 권한을 가진 사용자만 볼 수 있는 대시보드 페이지 템플릿의 경로를 반환합니다.
     */
    @GetMapping({"","/"})
    public String handleGetDashboardView(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return memberDetails.getRelationType() == RelationType.GUARDIAN ?
                "redirect:/dashboard/guardian" : "redirect:/dashboard/teacher";
    }

    /*
        D-1
     */
    @GetMapping("/guardian")
    public String handleGetParentDashboardView(
            @AuthenticationPrincipal MemberDetails memberDetails,
            Model model
    ) {
        ParentDashboardChildListDTO childProfiles = childService.getChildrenProfilesOf(memberDetails.getId());

        model.addAttribute("memberId", memberDetails.getId());
        model.addAttribute("memberName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
        model.addAttribute("childProfiles", childProfiles);
        return "dashboard/dashboardGuardianView";
    }

    //-------------------------------------------------------------------------------------------------------
    /**
     * v1.x.x-11
     * D-2
     * 수정 2025-03-03 / 김은진 / 모든 아이들의 기본 정보 조회
     *
     * @return 선생님 권한을 가진 사용자만 볼 수 있는 대시보드 페이지의 템플릿의 경로를 반환합니다.
     *         이 페이지에서는 하나의 페이지에서 여러 정보를 필요로 하므로
     *         관련된 API의 구현이 필요합니다.
     */
    @GetMapping("/teacher")
    public String handleGetTeacherDashboardView(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam(name="sortOption", defaultValue = "NAME") SortOption sortOption,
            Model model) {

        List<ChildBasicInfoDTO> childId = childService.getAllChildrenBasicInfo(sortOption);

        // 사용자 이름과 관계 유형을 모델에 추가
        model.addAttribute("userName", memberDetails.getName());
        model.addAttribute("relationType", memberDetails.getRelationType().getTypeNameKr());
        model.addAttribute("memberId", memberDetails.getId());

        model.addAttribute("childId", childId);
        model.addAttribute("currentSort", sortOption);

        return "dashboard/dashboardTeacherView";
    }



    /**
     * v1.x.x-12
     * 2025-03-04 김은진 / 특정아동의 프로필 정보 조회
     *
     * @param childId 조회할 아동의 ID
     * @return ChildBasicInfoDTO 아동의 기본 프로필 정보를 포함한 DTO
     */
    @ResponseBody
    @GetMapping("/{childId}/profile")
    public ResponseEntity<ChildBasicInfoDTO> getChildProfile(
            @PathVariable(name="childId") Long childId) {
        try {
            ChildBasicInfoDTO childInfo = childService.getChildBasicInfo(childId);

            // ✅ profileImgSrcUri가 null이면 기본 이미지 설정
            if (childInfo.getProfileImgSrcUri() == null || childInfo.getProfileImgSrcUri().isEmpty()) {
                childInfo.setProfileImgSrcUri("/images/default-profile.png");
            }

            return ResponseEntity.ok(childInfo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public enum SortOption {
        NAME("이름순"),
        BIRTH_DATE("생년월일순"),
        REGISTER_DATE("등록순");

        private final String displayName;

        SortOption(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    // 김은진 코드 끝 --------------------------------------------------------------------------------------------
}
