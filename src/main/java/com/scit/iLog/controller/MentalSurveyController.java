package com.scit.iLog.controller;

import com.scit.iLog.dto.mentalsurvey.ChildMentalStatsDTO;
import com.scit.iLog.dto.mentalsurvey.ChildNameDTO;
import com.scit.iLog.dto.mentalsurvey.response.MentalSurveyResponseChartDTO;
import com.scit.iLog.dto.mentalsurvey.response.MentalSurveyResponseDetailsDTO;
import com.scit.iLog.dto.mentalsurvey.response.MentalSurveyResponseInsertDTO;
import com.scit.iLog.dto.mentalsurvey.survey.MentalSurveyDetailsDTO;
import com.scit.iLog.dto.mentalsurvey.survey.MentalSurveyListDTO;
import com.scit.iLog.dto.mentalsurvey.survey.MentalSurveySelectInfoDTO;
import com.scit.iLog.service.MentalSurveyService;
import com.scit.iLog.service.child.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.scit.iLog.config.SecurityConfig.MemberDetails;

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
	private final MentalSurveyService mentalSurveyService;

	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * childOverView.html에서 설문목록 버튼 클릭 시 GET 요청을 처리한다.
	 * 우리 아이 설문 목록 페이지를 출력한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * S-1
	 * 이 페이지에서는 ajax를 이용하여 통계로 보여줍니다.
	 * 통계 요청은 모두 StatisticsController에서 처리힙니다.
	 */
	@GetMapping("/responses/stats")
	public String handleGetSurveysListPage() {
		return "children/mentalSurvey/mentalSurveyStatsView";
	}


	/**
	 * AJAX 호출을 위한 mentalsurvey 데이터 API 엔드포인트.
	 * 데이터는 시간순(오름차순)으로 정렬되어 JSON 형식으로 반환됩니다.
	 *
	 * 예시 반환 데이터: [{ "timestamp": "2024-02-01T00:00:00", "value": 120, "link": "https://example.com/data1" }, ...]
	 */
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@GetMapping("/responses/stats/data")
	public ChildMentalStatsDTO handleGetMentalSurveyStats(
			@PathVariable("childId") Long childId,
			@AuthenticationPrincipal MemberDetails memberDetails,
			@RequestParam(value = "startDate", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
			@RequestParam(value = "endDate", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate
	) {
		// 데이터베이스에서 시간순으로 정렬된 데이터 조회
		return mentalSurveyService.getMentalSurveyStatsBetween(childId, memberDetails.getId(), startDate, endDate);
	}

	/**
	 * 2025-02-25 김보경
	 * 설문조사 등록 페이지 바로가기
	 * S-2
	 * @return children/mentalSurvey/mentalSurveyDetailsView 뷰 페이지
	 */
	@GetMapping("/{mentalSurveyId}/responses/new")
	public String handleGetInsertMentalSurveyView(
			@PathVariable("childId") Long childId,
			@PathVariable("mentalSurveyId") String mentalSurveyId,
			Model model
	) {
		String childName = childService.getChildNameById(childId);
		MentalSurveyDetailsDTO mentalSurveyInfo = mentalSurveyService.getMetalSurveyDetails(mentalSurveyId);
		model.addAttribute("childName", childName);
		model.addAttribute("mentalSurvey", mentalSurveyInfo);
		return "children/mentalSurvey/mentalSurveyResponseInsertView";
	}

	/*
		S-2
	 */
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@PostMapping("/{mentalSurveyId}/responses/new")
	public String handlePostInsertMentalSurveyResponse(
			@PathVariable("childId") Long childId,
			@PathVariable("mentalSurveyId") String mentalSurveyId,
			@RequestBody MentalSurveyResponseInsertDTO mentalSurveyResponseInsertDTO,
			@AuthenticationPrincipal MemberDetails memberDetails
	) {
		Long memberId = memberDetails.getId();
		String responseId = mentalSurveyService.saveMentalSurveyResponse(
				childId,
				memberId,
				memberDetails.getRelationType(),
				mentalSurveyId,
				mentalSurveyResponseInsertDTO);
		return String.format("redirect:/children/%d/mentalSurveys/responses/%s/details", childId, responseId);
	}


	// 심리 설문 결과 조회 바로가기
	/* 2025-02-07 이도훈 주석 작성 및 메서드 명 수정.
	 * childOverView.html에서 설문작성 버튼 클릭 시 GET요청을 처리한다.
	 * 우리 아이 설문 종류 선택 페이지를 출력한다.
	 * 이 페이지에서 '심리 설문'과 '건강 문진'을 선택한다.
	 * URL, 메서드명, 리턴 값 수정.
	 * @return mentalSurveyResponseDetailsView.html
	 * S-3
	 */
	@GetMapping("/responses/{responseId}/details")
	public String handleGetMentalSurveyDetailsView(
			@PathVariable("childId") Long childId,
			@PathVariable("responseId") String responseId,
			Model model
	) {
		MentalSurveyResponseDetailsDTO responseDetails = mentalSurveyService.getResponseDetailsById(childId, responseId);
		model.addAttribute("responseDetails", responseDetails);
		return "children/mentalSurvey/mentalSurveyResponseDetailsView";
	}

	/*
		S-4
	 */
	@GetMapping("/selects")
	public String handleGetMentalSurveySelects(
			@PathVariable("childId") Long childId,
			Model model
	) {
		List<MentalSurveySelectInfoDTO> mentalSurveySelectInfo =  mentalSurveyService.getMentalSurveySelectInfo(childId);
		model.addAttribute("mentalSurveys", mentalSurveySelectInfo);
		return "children/mentalSurvey/mentalSurveySelectView";
	}

	/*
		S-X 설문 결과를 목록으로 보여주는 페이지
	 */
	@GetMapping("/surveyList")
	public String handleGetMentalSurveyListView(Model model) {
		MentalSurveyListDTO mentalSurveys = mentalSurveyService.getAllMentalSurveys();
		model.addAttribute("mentalSurveys", mentalSurveys);
		return "children/mentalSurvey/mentalSurveyListView";
	}

	// 시작 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		/**
		 * v1.x.x-10
		 * D-2 25/3/5 준성 MentalSurveyResponse 작업
		 * @param childId
		 * @param calendarNum
		 * @return
		 */
		@ResponseBody
		@PostMapping("/{calanderNum}/GetScores")
		public List<MentalSurveyResponseChartDTO> getMentalScores(@PathVariable(name="childId") Long childId,
																  @PathVariable(name="calanderNum", required = false) Long calendarNum)
		{
			// 주간(7), 월간(30), 반개월간(180) 구분
			calendarNum = (calendarNum == null)? 7 : calendarNum;

			List<MentalSurveyResponseChartDTO> mentalSurveyResponseChartDTOList = new ArrayList<>();

			if(calendarNum <= 7) 		mentalSurveyResponseChartDTOList= mentalSurveyService.getLastWeekData(childId);
			else if(calendarNum <= 30)  mentalSurveyResponseChartDTOList = mentalSurveyService.getLastMonthData(childId);
			else if(calendarNum <= 180) mentalSurveyResponseChartDTOList = mentalSurveyService.getLastYearData(childId);

			return mentalSurveyResponseChartDTOList;
		}

		// 끝 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

}
