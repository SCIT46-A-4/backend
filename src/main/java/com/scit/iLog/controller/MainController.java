package com.scit.iLog.controller;

import java.util.List;

import com.scit.iLog.domain.HelpEntity;
import com.scit.iLog.service.HelpService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	/**
	 * 컨트롤러에서 서비스를 사용할 준비를 하는 코드
	 * RequiredArgsConstructor를 통해 자동으로 생성자가 생성
	 * GuidesService 클래스 주입, 실제 사용할 객체 이름 guidesService
	 */
	private final HelpService guidesService;
	
	

	/**
	 * "기본/메인 페이지"를 보여주는 뷰를 반환하는 핸들러
	 * @return index.html 뷰 페이지
	 */
    @GetMapping({"","/"})
    public String handleIndex() {
        return "index";
    }
    
    
    
    /**
	 * "이용안내 목록 페이지"를 보여주는 뷰를 반환하는 핸들러
	 * @return "guides/guideListView" 뷰 페이지
	 */
    @GetMapping("/guides/guideListView")
    public String handleGetGuideListView(Model model) {
    	// 서비스에서 이용안내 데이터(ALL) 을 가져옴
    	List<HelpEntity> guides = guidesService.getAllGuides();
    	// html에서 th:each문을 이용하여 목록 반복 출력을 위해 리스트 저장
    	model.addAttribute("guides", guides);
    	
        return "guides/guideListView";
    }
    
    
    
}
