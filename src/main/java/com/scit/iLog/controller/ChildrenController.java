package com.scit.iLog.controller;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.dto.PageResponse;
import com.scit.iLog.dto.child.*;
import com.scit.iLog.service.analysis.AnalysisService;
import com.scit.iLog.service.child.ChildRecordService;
import com.scit.iLog.service.child.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	 * -> ChildService로 옮겼습니다. 아이의 기본정보이기 때문에.
	 */
	private final ChildService childService;
	private final ChildRecordService childRecordService;
	private final AnalysisService analysisService;

	/*
	 *
	 * C-1
	 * 25/2/11 준: api-22 아이 정보 등록 페이지 반환
	 */
	@GetMapping("/new")
	public String handleGetChildInsertView() {
		return "children/basicInfoInsertView";
	}

	/*
	 * 
	 * C-1
	 * 25/2/11 준: api-23 입력된 아동 정보를 저장
	 * 반환: 아이 상세 정보페이지
	 */
	@ResponseBody
	@PostMapping("/new")
	public ChildBasicInfoInsertResponseDTO handlePostChildBasicInfoInsert(
			@ModelAttribute ChildBasicInfoInsertDTO childBasicInfoInsertDTO,
			RedirectAttributes redirectAttributes
	) {
		Long childId = childService.saveBasicInfo(childBasicInfoInsertDTO);
		redirectAttributes.addAttribute("childId", childId);
		log.info("childId: {}", childId.toString());
		return new ChildBasicInfoInsertResponseDTO(childId);
	}

	/**
	 * 
	 * 2025-02-25 김보경
	 * 
	 * 설문조사 등록 페이지 바로가기
	 * 
	 * c-2
	 * 
	 * @param childId
	 * 
	 * @param model
	 * 
	 * @return children/mentalSurvey/mentalSurveyDetailsView 뷰 페이지
	 * 
	 */
	@GetMapping("/{childId}/mentalSurvey/new")
	public String handleGetMentalSurveyDetailsView(
			@PathVariable("childId") Long childId,
			Model model
	) {
		ChildBasicInfoDTO childBasicInfo = childService.getBasicInfoById(childId);
		model.addAttribute("childBasicInfo", childBasicInfo);
		return "children/mentalSurvey/mentalSurveyDetailsView";
	}

	/**
	 *
	 * C-2
	 * 특정 ID에 해당하는 아동 기본 정보를 조회하여 "아동 기본 정보 조회 페이지" 뷰에 전달하는 핸들러
	 * @param childId
	 * @param model
	 * @return children/basicInfoDetailsView.html 뷰 페이지
	 * 서비스 레이어에서 ChildDetailsDto를 가져와 뷰에 전달
	 */
	@GetMapping("/{childId}/details")
	public String handleGetChildBasicInfoView(
			@PathVariable("childId") Long childId,
			Model model
	) {
		// ID에 해당하는 아동 기본 정보를 조회
		ChildBasicInfoDTO childBasicInfo = childService.getBasicInfoById(childId);
		// 조회한 데이터를 뷰(Model)에 추가
		model.addAttribute("childBasicInfo", childBasicInfo);
		log.info("childProfileImgSrc in dto: {}", childBasicInfo.getProfileImgSrcUri());
		log.info("gender in dto: {}", childBasicInfo.getGender());
		return "children/basicInfoDetailsView";
	}

	/*
	 * C-2
	 * 25/2/11 api-28: 아동 삭제요청, return: 대쉬보드 Page
	 */
	@DeleteMapping("/{childId}")
	@ResponseBody
	public boolean deleteChildInsertView(@PathVariable("childId") Long childId) {
		childService.deleteChildById(childId);
		return true;
	}

	/*
	 * C-3
	 * Exception은 checked Exception이기 때문에 어디선가 try catch로 반드시 잡아줘야합니다.
	 * 25/2/11 준 api-29 수정: 아동 정보수정페이지 요청
	 */
	@GetMapping("/{childId}/basicInfo/edit")
	public String handleGetChildBasicInfoUpdateView(
			@PathVariable("childId") Long childId,
			Model model
	) {
		ChildBasicInfoDTO childBasicInfo = childService.getBasicInfoById(childId);
		log.debug("수정 페이지 로딩 - 가정환경 데이터: {}", childBasicInfo.getFamilyBackGrounds());
		model.addAttribute("childBasicInfo", childBasicInfo);
		return "children/basicInfoUpdateView";
	}

	/*
	 * 
	 * C-3
	 * 25/2/11 준 api-30 아이 정보 수정
	 */
	@PostMapping("/{childId}/details/edit")
	public String handlePostChildBasicInfoUpdate(
			@PathVariable("childId") Long childId,
			@ModelAttribute ChildBasicInfoUpdateDTO updateDTO) {
		log.info("수정 요청 - 가정환경 데이터: {}", updateDTO.familyBackGrounds());
		childService.updateChildBasicInfo(childId, updateDTO);
		return String.format("redirect:/children/%d/details", childId);
	}

	/**
	 * 2025-02-25 / 김보경
	 * c-2 페이지 바로가기 버튼을 작성하다 수정
	 * PathVariable 및 model 추가
	 * 아동 상세 정보 등록 페이지를 조회
	 * 25/2/11 준 : api-24 아이 상제정보 등록페이지 반환
	 * @return children/records/childRecordInsertView 뷰 페이지
	 */
	@GetMapping("/{childId}/records/new")
	public String handleGetChildRecordInsertView(
			@PathVariable("childId") Long childId,
			Model model
	) {
		ChildBasicInfoDTO childBasicInfo = childService.getBasicInfoById(childId);
		model.addAttribute("childBasicInfo", childBasicInfo);
		return "children/records/childRecordInsertView";
	}

	/*
	 * C-4
	 * 아이 신체 정보 저장 요청을 처리
	 */
	@PostMapping("/{childId}/records/new")
	public String handlePostChildRecordInsert(
			@PathVariable("childId") Long childId,
			@AuthenticationPrincipal MemberDetails memberDetails,
			@ModelAttribute ChildRecordInsertDTO childRecordInsertDTO
	) {
		Long childRecordId = childRecordService.saveChildRecord(childId, memberDetails.getId(), childRecordInsertDTO);
		return String.format("redirect:/children/%d/records/%d", childId, childRecordId);
	}

	/*
	 * C-4, C-7
	 * 문진표에서 신체 정보 가져오기 요청을 처리
	 */
	@ResponseBody
	@PostMapping("/healthCheck/recordData")
	public ChildRecordExtraction handlePostHealthCheckImg(
			@RequestParam("healthCheckImg") MultipartFile healthCheckImg
	) {
		return analysisService.getExtractChildRecordData(healthCheckImg);
	}

	/*
	 * C-5
	 */
	@GetMapping("/{childId}/records/{recordId}")
	public String handleGetChildRecordView(
			@PathVariable("childId") Long childId,
			@PathVariable("recordId") Long recordId,
			Model model
	) {
		ChildRecordDTO childRecordDTO = childRecordService.findOneById(recordId);
		model.addAttribute("childRecord", childRecordDTO);
		return "children/records/childRecordDetailsView";
	}

	/*
	 * C-6
	 */
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{childId}/recordList")
	public String handleGetChildRecordListView(
			@PathVariable("childId") Long childId,
			Model model,
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "10") int limit,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "DESC") String direction
	) {
		// 정렬 방향 설정
		Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
		Sort sort = Sort.by(sortDirection, sortBy);
		// 페이지 요청 객체 생성
		Pageable pageable = PageRequest.of(offset, limit, sort);
		// 서비스를 통해 페이징된 데이터 조회
		Page<ChildRecordListItemDTO> childRecordsPage = childRecordService.findPagedChildRecords(childId, pageable);
		// 응답 데이터 구성
		model.addAttribute("childRecordsPage", PageResponse.<ChildRecordListItemDTO>builder()
				.content(childRecordsPage.getContent())
				.pageNumber(childRecordsPage.getNumber())
				.pageSize(childRecordsPage.getSize())
				.totalElements(childRecordsPage.getTotalElements())
				.totalPages(childRecordsPage.getTotalPages())
				.last(childRecordsPage.isLast())
				.build());
		return "children/records/childRecordListView";
	}

	/*
	 * C-7
	 */
	@GetMapping("/{childId}/records/{childRecordId}/edit")
	public String handleGetChildRecordUpdateView(
			@PathVariable("childId") Long childId,
			@PathVariable("childRecordId") Long childRecordId,
			Model model
	) {
		ChildRecordDTO childRecordDTO = childRecordService.findOneByChildIdAndRecordId(childId, childRecordId);
		model.addAttribute("childRecord", childRecordDTO);
		return "children/records/childRecordUpdateView";
	}

	/*
	 * C-7
	 */
	@PostMapping("/{childId}/records/{childRecordId}/edit")
	public String handlePostChildRecordUpdate(
			@PathVariable("childId") Long childId,
			@PathVariable("childRecordId") Long childRecordId,
			@ModelAttribute ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO
	) {
		childRecordService.updateChildRecord(childRecordId, childRecordUpdateRequestDTO);
		return String.format("redirect:/children/%d/records/%d", childId, childRecordId);
	}
}