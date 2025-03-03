package com.scit.iLog.controller;

import com.scit.iLog.dto.child.ChildBasicInfoDTO;
import com.scit.iLog.service.child.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/* 
 * 2025-02-07 이도훈
 * 우리 아이 설문에 대한 요청을 처리하는 컨트롤러.
 * 설문 목록을 조회, 새로운 설문을 작성, 수정 및 삭제하는 기능 제공.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/children/{childId}/mentalSurveys")
public class MentalSurveyController {
	private final ChildService childService;

	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * childOverView.html에서 설문목록 버튼 클릭 시 GET 요청을 처리한다.
	 * 우리 아이 설문 목록 페이지를 출력한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return mentalSurveyListView.html
	 *
	 * S-1
	 * 이 페이지에서는 ajax를 이용하여 통계로 보여줍니다.
	 * 통계 요청은 모두 StatisticsController에서 처리힙니다.
	 */
    @GetMapping("/surveyList")
    public String handleGetSurveysListPage() {
		return "children/mentalSurveys/mentalSurveyListView";
    }

	/**
	 * 2025-02-25 김보경
	 * 설문조사 등록 페이지 바로가기
	 * S-2
	 * @return children/mentalSurvey/mentalSurveyDetailsView 뷰 페이지
	 */
	@GetMapping("/new")
	public String handleGetMentalSurveyInsertView(
			@PathVariable("childId") Long childId,
			Model model
	) {
		ChildBasicInfoDTO childBasicInfo = childService.getBasicInfoById(childId);
		model.addAttribute("childBasicInfo", childBasicInfo);
		return "children/mentalSurvey/mentalSurveyDetailsView";
	}

	/*
		S-2
		@TODO 심리설문을 저장하는 post 요청을 처리하는 메서드가 필요합니다.
	 */


    // 심리 설문 결과 조회 바로가기
	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * childOverView.html에서 설문작성 버튼 클릭 시 GET요청을 처리한다.
	 * 우리 아이 설문 종류 선택 페이지를 출력한다.
	 * 이 페이지에서 '심리 설문'과 '건강 문진'을 선택한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return mentalSurveyDetailsView.html
	 *
	 * S-3
	 */
    @GetMapping("/{mentalSurveyId}/details")
    public String handleGetMentalSurveyDetailsView() {
		return "children/mentalSurveys/mentalSurveyDetailsView";
    }
}
