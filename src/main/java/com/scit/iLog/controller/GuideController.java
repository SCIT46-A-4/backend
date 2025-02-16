package com.scit.iLog.controller;

import com.scit.iLog.domain.GuideEntity;
import com.scit.iLog.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/*
	이 컨트롤러는 다음의 페이지에서 필요한 요청을 처리합니다.
	- 이용안내 관련 페이지 모음
 */
@Controller("/guides")
@RequiredArgsConstructor
public class GuideController {
    /**
     * 컨트롤러에서 서비스를 사용할 준비를 하는 코드
     * RequiredArgsConstructor를 통해 자동으로 생성자가 생성
     * GuidesService 클래스 주입, 실제 사용할 객체 이름 guidesService
     */
    private final GuideService guidesService;

    /**
     * "이용안내 목록 페이지"를 보여주는 뷰를 반환하는 핸들러
     * @return "guides/guideListView" 뷰 페이지
     */
    @GetMapping
    public String handleGetGuideListView(Model model) {
        // 서비스에서 이용안내 데이터(ALL) 을 가져옴
        List<GuideEntity> guides = guidesService.getAllGuides();
        // html에서 th:each문을 이용하여 목록 반복 출력을 위해 리스트 저장
        model.addAttribute("guides", guides);
        return "guides/guideListView";
    }
    /*
        @TODO 서치 기능 추가해야함.
        ajax로 구현합니다.
     */

}
