package com.scit.iLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/* 
 * 2025-02-07 이도훈
 * 우리 아이 설문에 대한 요청을 처리하는 컨트롤러.
 * 설문 목록을 조회, 새로운 설문을 작성, 수정 및 삭제하는 기능 제공.
 */
@Controller
@RequiredArgsConstructor
public class SurveyController {
	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * childOverView.html에서 설문목록 버튼 클릭 시 GET 요청을 처리한다.
	 * 우리 아이 설문 목록 페이지를 출력한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return surveyListView.html
	 *  */
    @GetMapping("/survey/surveysList")
    public String handleGetSurveysListPage() {
        return "children/survey/surveyListView";
    }

    // 설문쓰기_선택 바로가기
	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * childOverView.html에서 설문작성 버튼 클릭 시 GET요청을 처리한다.
	 * 우리 아이 설문 종류 선택 페이지를 출력한다.
	 * 이 페이지에서 '심리 설문'과 '건강 문진'을 선택한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return surveySelect.html
	 *  */
    @GetMapping("/survey/surveySelect")
    public String handleGetSurveySelectPage()
    {
        return "/children/survey/surveySelectView";
    }

    // 설문쓰기_신체 바로가기
	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * surveySelectView.html에서 건강 문진 버튼 클릭 시 GET방식으로 요청을 처리한다.
	 * 건강 문진 설문 작성 페이지를 출력한다.
	 * 이 페이지에서 건강과 관련된 정보를 입력한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return /health/insertView.html
	 *  */
    @GetMapping("/survey/surveyHealth")
    public String handleInsertSurveyHealthPage()
    {
        return "/children/survey/health/insertView";
    }

    // 설문쓰기_심리 바로가기
	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * surveySelectView.html에서 심리 설문 버튼 클릭 시 GET방식으로 요청을 처리한다.
	 * 심리 설문 작성 페이지를 출력한다.
	 * 이 페이지에서 심리 설문 정보를 입력한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return /mental/insertView.html
	 *  */
    @GetMapping("/survey/surveyMental")
    public String handleInsertSurveyMentalPage()
    {
        return "/children/survey/mental/insertView";
    }
}
