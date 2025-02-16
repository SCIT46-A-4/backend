package com.scit.iLog.controller;

import java.time.LocalDate;

import com.scit.iLog.dto.child.ChildRecordDTO;
import com.scit.iLog.service.ChildRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.child.Gender;
import com.scit.iLog.dto.ChildDTO;
import com.scit.iLog.dto.child.ChildDetailsDto;
import com.scit.iLog.service.ChildService;
import com.scit.iLog.service.InfoDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/children")
public class ChildrenController {

	/* 2025-02-06 infoDetailsService 선언 */
	/**
	 * 컨트롤러에서 서비스를 사용할 준비를 하는 코드
	 * RequiredArgsConstructor를 통해 자동으로 생성자가 생성
	 * InfoDetailsService 클래스 주입, 실제 사용할 객체 이름 infoDetailsService
	 */
	private final InfoDetailsService infoDetailsService;
	private final ChildService childService;
	private final ChildRecordService childRecordService;

	/**
	 * "우리 아이 일기 감정 분석 그래프" 페이지를 보여주는 뷰를 반환하는 핸들러
	 *
	 * @return children/statistics/detailsView.html 뷰 페이지
	 */
	@GetMapping("/statisticsDetails")
	public String handleGetStatisticsDetailsView() {
		return "/children/statistics/detailsView";
	}


	/**
	 * "일기 상세" 페이지를 보여주는 뷰를 반환하는 핸들러
	 *
	 * @return children/diaries/diaryDetailsView.html 뷰 페이지
	 */
	@GetMapping("/diaryDetails")
	public String handleGetDiaryDetailsView() {
		return "/children/diaries/diaryDetailsView";
	}


	/**
	 * "내 아이 한눈에 보기" 페이지를 보여주는 뷰를 반환하는 핸들러
	 *
	 * @return children/childOverviewView.html 뷰 페이지
	 */
	@GetMapping("/details")
	public String handleGetChildOverviewView() {
		return "/children/childOverviewView";
	}

	/**
	 * 특정 ID에 해당하는 아동 기본 정보를 조회하여 "아동 정보 상세 페이지" 뷰에 전달하는 핸들러
	 *
	 * @param id
	 * @param model
	 * @return children/childDetailsView.html 뷰 페이지
	 * 서비스 레이어에서 ChildDetailsDto를 가져와 뷰에 전달
	 */
	@GetMapping("/infoDetails")
	public String handleGetChildDetailsView(@RequestParam(name = "id") Long id, Model model) {
		// ID에 해당하는 아동 상세 정보를 조회
		ChildDetailsDto childDetailsDto = infoDetailsService.seletInfoDetails(id);

		// 조회한 데이터를 뷰(Model)에 추가
		model.addAttribute("childDetailsDto", childDetailsDto);

		return "/children/childDetailsView";
	}

	// 흐름도: (아이 등록페이지 버튼)    -> 등록페이지 반환
	//	     등록페이지(등록하기)      -> 등록완료(아이 상세정보 페이지 반환)
	// 		 아이 상세 정보 수정 페이지 -> 아이 정보 수정 페이지 반환
	// 

	// 25/2/11 준: api-22 아이 정보 등록 페이지 반환
	@GetMapping("/new")
	public String handleGetChildInsertView() {
		return "children/insertView";
	}

	// 25/2/11 준: api-23 입력된 아동 정보를 저장 
	// 반환: 아이 상세 정보페이지
	@PostMapping("/new")
	public String saveChildData(@ModelAttribute ChildDTO childDto,
								RedirectAttributes redirectAttributes) {
		Long childId = childService.save(childDto);

		redirectAttributes.addAttribute("childId", childId);

		return "redirect:/children/{childId}/details";
	}

	/*
	 * 아동 상세 정보 등록 페이지를 조회
	 * 25/2/11 준 : api-24 아이 상제정보 등록페이지 반환
	 * @return children/childDetailsInsertView 뷰 페이지
	 */
	@GetMapping("/detailsInsert")
	public String handleGetChildDetailsInsertView() {
		return "/children/childDetailsInsertView";
	}

	// 25/2/12 ㅈ: api-26 아이 정보 상세페이지 요청
	@GetMapping("/{childId}/details")
	public String handleGetChildrenDetailView(
			@PathVariable(name = "childId") Long id,
			Model model
	) throws Exception {
		// DB에 저장된 아이 정보 꺼내오기
		ChildDTO _dto = childService.findById(id);
		// child 데이터 찾아서 반환, 삼항연산자 null 체크 있음 
		model.addAttribute("child", (_dto != null) ? _dto : "데이터를 찾을 수 없습니다.");

		return "/children/childDetailsView";
	}

	// 25/2/11 api-28: 아동 삭제요청, return: 대쉬보드 Page
	@DeleteMapping("/{childId}")
	@ResponseBody
	public String deleteChildInsertView(@PathVariable(name = "childId") Long childId) {
		try {
			childService.deleteById(childId);
			return "ok";
		} catch (Exception e) {
			return "no";
		}
	}

	// 25/2/11 준 api-29 수정: 아동 정보수정페이지 요청
	@GetMapping("/{childId}/details/edit")
	public String handleGetChildDetailsUpdateView(@PathVariable(name = "childId") Long childId,
											Model model) throws Exception {
		model.addAttribute("child", childService.findById(childId));
		return "/children/childDetailsUpdateView";
	}

	// 25/2/11 준 api-30 아이 정보 수정
	@PostMapping("/{childId}/details/edit")
	public String tempupdateName(@PathVariable(name = "childId") Long childId,
								 @ModelAttribute ChildDTO childDto,
								 RedirectAttributes rtrt) {
		// 아이 정보 수정
		childService.updateChildData(childId, childDto);

		// RedirectAttributes에 파라미터 추가
		rtrt.addAttribute("childId", childId);

		return "redirect:/children/{childId}/details";
	}

	@GetMapping("/{childId}/records/{recordId}")
	public String handleGetChildRecordView(
			@PathVariable("childId") Long childId,
			@PathVariable("recordId") Long recordId,
			Model model
	) {
		ChildRecordDTO childRecordDTO = childRecordService.findOneByChildIdAndRecordId(childId,recordId);
		model.addAttribute("childRecord", childRecordDTO);
		return "children/childRecordView";
	}
}
