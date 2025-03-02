package com.scit.iLog.controller;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.dto.PageResponse;
import com.scit.iLog.dto.child.*;
import com.scit.iLog.service.analysis.AnalysisService;
import com.scit.iLog.service.child.ChildRecordService;
import com.scit.iLog.service.child.ChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import com.scit.iLog.dto.child.ChildBasicInfoDTO;
import com.scit.iLog.dto.child.ChildBasicInfoInsertDTO;
import com.scit.iLog.dto.child.ChildBasicInfoUpdateDTO;
import com.scit.iLog.dto.child.ChildRecordDTO;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import com.scit.iLog.dto.child.ChildRecordInsertDTO;
import com.scit.iLog.dto.child.ChildRecordListItemDTO;
import com.scit.iLog.dto.child.ChildRecordResponseDTO;
import com.scit.iLog.dto.child.ChildRecordUpdateRequestDTO;
import com.scit.iLog.dto.child.ChildRecordUpdateResponseDTO;
import com.scit.iLog.dto.child.ChildRecordUpdateViewDTO;
import com.scit.iLog.util.FilePathUtil;
import com.scit.iLog.util.PageNavigator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/children")
public class ChildrenController {

	/* 2025-02-06 infoDetailsService 선언 */
	/**
	 * 컨트롤러에서 서비스를 사용할 준비를 하는 코드
	 * RequiredArgsConstructor를 통해 자동으로 생성자가 생성
	 */
	private final ChildService childService;
	private final ChildRecordService childRecordService;
	private final AnalysisService analysisService;
	private final FilePathUtil filePathUtil;

	/*
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
	 * 2025-02-25 김보경
	 * 설문조사 등록 페이지 바로가기
	 * C-2
	 * @param childId
	 * @param model
	 * @return children/mentalSurvey/mentalSurveyDetailsView 뷰 페이지
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
		model.addAttribute("childId", childId);
		return "children/records/childRecordInsertView";
	}

	/*
	 * C-4
	 * 아이 신체 정보 저장 요청을 처리
	 */
	@PostMapping("/{childId}/records/new")
	@ResponseBody
	public ChildRecordResponseDTO handlePostChildRecordInsert(	//String을 ChildRecordResponseDTO로 변경
	        @PathVariable("childId") Long childId,
	        @AuthenticationPrincipal MemberDetails memberDetails,
	        @ModelAttribute ChildRecordInsertDTO childRecordInsertDTO
	) {
	    // 아동 신체 정보 저장
	    Long childRecordId = childRecordService.saveChildRecord(childId, memberDetails.getId(), childRecordInsertDTO);
	    // 업로드 완료 후 클라이언트에서 리다이렉트
	    return new ChildRecordResponseDTO(true, String.format("/children/%d/records/%d", childId, childRecordId));
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

	/**
	 * API : v1.x.x-2
	 * C-5 수정 25/2/24 준
	 * getMapping( /{childId}/records/{recordId} -> /{recordId}/records/)
	 * @param recordId
	 * @param childId
	 * @param model
	 * @return
	 */
	@GetMapping("/{childId}/records/{recordId}")
	public String handleGetChildRecordView(
			@PathVariable("recordId") Long recordId,
			@PathVariable("childId") Long childId,
			Model model
    ) {
		ChildRecordDTO childRecordDTO = childRecordService.findOneById(recordId);
		String savedFileName = childRecordService.FindHealthCehckByChildRecordId(recordId).getSavedFileName();
		model.addAttribute("childRecord", childRecordDTO);
		model.addAttribute("childId", childId);
		model.addAttribute("savedFileName", savedFileName);

		return "children/records/childRecordDetailsView";
	}

	/**
	 * API : v1.x.x-5
	 * C-5 정준성
	 * @param id
	 * @param response
	 * @return
	 */
	@GetMapping("/download")
	public String download(@RequestParam(name = "childrenId") Long id, HttpServletResponse response) {
		// HttpServletResponse
		// 스프링에서 자동으로 전달해주는 값으로, 클라이언트(사용자)에게 보낼 헤더값을 만든다
		// 이 객체는 개발자가 새롭게 만들 수 없다. 자동으로 스프링에서 http요청 처리과정을 담당함

		// 1. DB에서 데이터를 찾는다
		System.out.println("download 접속");
		HealthCheckEntity healthCheckEntity = childRecordService.FindHealthCehckByChildRecordId(id);

		String savedFileName = healthCheckEntity.getSavedFileName();
		String originalFileName = healthCheckEntity.getOriginalFileName();

		try {
			// 파일명이 깨질 우려가 있어서 utf-8로 인코딩한다
			String tempName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString());

			// 다운로드 명령 설정, 사용자가 받을 파일명 지정
			response.setHeader("Content-Disposition", "attachment;filename=" + tempName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String fullPath = filePathUtil.childHealthCheckImgUploadPath() + savedFileName;
		System.out.println("fullPath: " + fullPath);
		FileInputStream filein = null;
		ServletOutputStream fileout = null;

		try {
			// 서버에서 실제 파일의 경로를 조회해서 데이터를 읽는다
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();

			// fileIn -> fileOut
			FileCopyUtils.copy(filein, fileout);

			fileout.close();
			filein.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * API : v1.x.x-3
	 * C-6 이도훈 2025-02-24~26
	 * http://localhost:9900/children/1/recordList
	 * offset → page 변경
	 * @param childId   아이 ID
	 * @param model     모델
	 * @param page      현재 페이지
	 * @param limit     한 페이지당 아이템 수
	 * @param sortBy    정렬 기준
	 * @param direction 정렬 방향
	 * @return 신체 기록 목록 뷰
	 */
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{childId}/recordList")
	public String handleGetChildRecordListView(
			@PathVariable("childId") Long childId,
			@RequestParam(name = "page", defaultValue = "0") int page, // offset → page 변경
			@RequestParam(name = "limit", defaultValue = "5") int limit,
			@RequestParam(name = "sortBy", defaultValue = "registerDate") String sortBy,
			@RequestParam(name = "direction", defaultValue = "DESC") String direction,
			Model model
	) {
		// 정렬 방향 변환 (예외 처리 추가)
		Sort.Direction sortDirection;
		try {
			sortDirection = Sort.Direction.valueOf(direction.toUpperCase());
		} catch (IllegalArgumentException e) {
			sortDirection = Sort.Direction.DESC;
		}
		Sort sort = Sort.by(sortDirection, sortBy);

		// 페이지 요청 객체 생성
		Pageable pageable = PageRequest.of(page, limit, sort); // offset → page 변경

		// 아이 이름을 갖고오기
		ChildBasicInfoDTO childName = childService.findById(childId);

		// 서비스를 통해 페이징된 데이터 조회
		Page<ChildRecordListItemDTO> childRecordsPage = childRecordService.findPagedChildRecords(childId, pageable);

		// **PageNavigator 객체 생성 (그룹당 5페이지씩)**
		PageNavigator pageNavigator = PageNavigator.of()
				.groupSize(5) // 페이지 네비게이션 그룹 크기 (5개씩)
				.itemsPerPage(childRecordsPage.getSize())
				.currentPage(childRecordsPage.getNumber())
				.totalPages(childRecordsPage.getTotalPages())
				.build();

		// 응답 데이터 구성
		model.addAttribute("childRecordsPage",
				PageResponse
					.<ChildRecordListItemDTO>builder()
					.content(childRecordsPage.getContent())
					.pageNumber(childRecordsPage.getNumber())
					.pageSize(childRecordsPage.getSize())
					.totalElements(childRecordsPage.getTotalElements())
					.totalPages(childRecordsPage.getTotalPages())
					.last(childRecordsPage.isLast())
					.build()
					);

		// 모델 추가
		model.addAttribute("childName", childName);
		model.addAttribute("pageNavigator", pageNavigator);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("direction", direction);

		return "children/records/childRecordListView";
	}

	/**
	 * API : v1.x.x-4 아이의 기록 삭제
	 * C-6 이도훈 2025-02-24~26
	 * @param childId  아이 ID
	 * @param recordId 기록 ID
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT) // ✅ 204 No Content 응답 설정
	@DeleteMapping("/{childId}/records/{recordId}/delete")
	public void deleteChildRecord(
			@PathVariable("childId") Long childId,
			@PathVariable("recordId") Long recordId
	) {
		childRecordService.deleteChildRecord(recordId);
	}

	/**
	 * API : v1.x.x-8
	 * C-7 이도훈
	 * @param childId
	 * @param childRecordId
	 * @param model
	 * @return
	 */
	@GetMapping("/{childId}/records/{childRecordId}/edit")
	public String handleGetChildRecordUpdateView(
			@PathVariable("childId") Long childId,
			@PathVariable("childRecordId") Long childRecordId,
			Model model
	) {
		ChildRecordUpdateViewDTO childRecordUpdateViewDTO = childRecordService
			.findOneByUpdateChildIdAndRecordId(childId, childRecordId);

		model.addAttribute("childRecord", childRecordUpdateViewDTO);

		return "children/records/childRecordUpdateView";
	}

	/**
	 * API : v1.x.x-9
	 * C-7 이도훈
	 * @param childId
	 * @param childRecordId
	 * @param childRecordUpdateRequestDTO
	 * @return
	 */
	@ResponseBody
	@PostMapping("/{childId}/records/{childRecordId}/edit")
	public ChildRecordUpdateResponseDTO handlePostChildRecordUpdate(
			@PathVariable("childId") Long childId,
			@PathVariable("childRecordId") Long childRecordId,
			@ModelAttribute("formData") ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO
	) {
		childRecordService.updateChildRecord(childRecordId, childRecordUpdateRequestDTO);

		return new ChildRecordUpdateResponseDTO(true, String.format("/children/%d/records/%d", childId, childRecordId));
	}
}
