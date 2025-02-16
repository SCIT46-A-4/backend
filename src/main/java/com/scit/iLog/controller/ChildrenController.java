package com.scit.iLog.controller;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.dto.child.*;
import com.scit.iLog.dto.PageResponse;
import com.scit.iLog.service.analysis.AiAnalysisService;
import com.scit.iLog.service.child.ChildRecordService;
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

import com.scit.iLog.service.child.ChildService;

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
	 * -> ChildService로 옮겼습니다. 아이의 기본정보이기 때문에.
	 */
	private final ChildService childService;
	private final ChildRecordService childRecordService;
	private final AiAnalysisService aiAnalysisService;

	// 흐름도: (아이 등록페이지 버튼)    -> 등록페이지 반환
	//	     등록페이지(등록하기)      -> 등록완료(아이 상세정보 페이지 반환)
	// 		 아이 상세 정보 수정 페이지 -> 아이 정보 수정 페이지 반환
	// 

	// 25/2/11 준: api-22 아이 정보 등록 페이지 반환
	/*
		C-1
	 */
	@GetMapping("/new")
	public String handleGetChildInsertView() {
		return "children/insertView";
	}

	// 25/2/11 준: api-23 입력된 아동 정보를 저장 
	// 반환: 아이 상세 정보페이지
	/*
		C-1
	 */
	@PostMapping("/new")
	public String handlePostChildInsert(
			@ModelAttribute ChildBasicInfoDTO childBasicInfoDto,
			RedirectAttributes redirectAttributes
	) {
		Long childId = childService.saveBasicInfo(childBasicInfoDto);
		redirectAttributes.addAttribute("childId", childId);

		return "redirect:/children/{childId}/details";
	}

	/**
	 * 특정 ID에 해당하는 아동 기본 정보를 조회하여 "아동 기본 정보 조회 페이지" 뷰에 전달하는 핸들러
	 *
	 * @param childId
	 * @param model
	 * @return children/childDetailsView.html 뷰 페이지
	 * 서비스 레이어에서 ChildDetailsDto를 가져와 뷰에 전달
	 * @TODO RequestParam이 아닌 PathVariable로 받아야합니다.
	 * C-2
	 */
	@GetMapping("{childId}/basicInfo")
	public String handleGetChildBasicInfoView(@PathVariable("childId") Long childId, Model model) {
		// ID에 해당하는 아동 기본 정보를 조회
		ChildBasicInfoDTO childBasicInfo = childService.selectBasicInfoById(childId);

		// 조회한 데이터를 뷰(Model)에 추가
		model.addAttribute("childBasicInfo", childBasicInfo);

		return "/children/childDetailsView";
	}

	// 25/2/12 ㅈ: api-26 아이 정보 상세페이지 요청
	/*
		C-2
		@TODO 이 메서드는 위의 것과 중복되므로 삭제 예정
	 */
	@GetMapping("/{childId}/details")
	public String handleGetChildrenDetailView(
			@PathVariable(name = "childId") Long id,
			Model model
	) throws Exception {
		// DB에 저장된 아이 정보 꺼내오기
		ChildBasicInfoDTO _dto = childService.findById(id);
		// child 데이터 찾아서 반환, 삼항연산자 null 체크 있음
		/*
			@TODO ??? 이렇게 아예 다른 타입을 넣어버리면 안됩니다!!!!
		 */
		model.addAttribute("child", (_dto != null) ? _dto : "데이터를 찾을 수 없습니다.");

		return "/children/childDetailsView";
	}

	// 25/2/11 api-28: 아동 삭제요청, return: 대쉬보드 Page
	/*
		C-2
		@TODO 이렇게 boolean값인데 문자열로 응답을 주는 것은 옳지 않습니다!!!
	 */
	@DeleteMapping("/{childId}")
	@ResponseBody
	public String deleteChildInsertView(@PathVariable("childId") Long childId) {
		try {
			childService.deleteById(childId);
			return "ok";
		} catch (Exception e) {
			return "no";
		}
	}

	// 25/2/11 준 api-29 수정: 아동 정보수정페이지 요청
	/*
		C-3
		@TODO 이렇게 Exception 막 던지면 안됩니다!!
		Exception은 checked Exception이기 때문에 어디선가 try catch로 반드시 잡아줘야합니다.
	 */
	@GetMapping("/{childId}/basicInfo/edit")
	public String handleGetChildDetailsUpdateView(
			@PathVariable("childId") Long childId,
			Model model
	) throws Exception {
		model.addAttribute("child", childService.findById(childId));
		return "/children/childDetailsUpdateView";
	}

	// 25/2/11 준 api-30 아이 정보 수정
	/*
		C-3
		이렇게 파라미터가 3개 이상이 되면 한줄씩 개행해서 보기좋게!!!!
		컨트롤러 메서드 이름 잘 지읍시다. ^^
		이러면 진짜 회사 짤립니다.^^
		@TODO 컨트롤러 메서드 이름 수정해야합니다!!
	 */
	@PostMapping("/{childId}/basicInfo/edit")
	public String tempupdateName(
			@PathVariable(name = "childId") Long childId,
			@ModelAttribute ChildBasicInfoDTO childBasicInfoDto,
			RedirectAttributes redirectAttributes
	) {
		// 아이 정보 수정
		childService.updateChildData(childId, childBasicInfoDto);

		/*
			@TODO 이거는 필요없는 코드입니다...
			리다이렉트할 때 아이디값이 필요하다면 url에 바로 넣어주면됩니다.
		 */
		// RedirectAttributes에 파라미터 추가
		// redirectAttributes.addAttribute("childId", childId);

		return String.format("redirect:/children/%d/basicInfo", childId);
	}

	/*
	 * 아동 상세 정보 등록 페이지를 조회
	 * 25/2/11 준 : api-24 아이 상제정보 등록페이지 반환
	 * @return children/childDetailsInsertView 뷰 페이지
	 * C-4
	 */
	@GetMapping("/{childId}/records/new")
	public String handleGetChildRecordInsertView() {
		return "/children/childDetailsInsertView";
	}

	/*
		C-4
		아이 신체 정보 저장 요청을 처리
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
		C-4, C-7
		문진표에서 신체 정보 가져오기 요청을 처리
	 */
	@ResponseBody
	@PostMapping("/healthCheck/recordData")
	public ChildRecordInsertDTO handlePostHealthCheckImg(
			@RequestParam("healthCheckImg") MultipartFile healthCheckImg
	) {
		return aiAnalysisService.extractChildRecordDataFromImg(healthCheckImg);
	}

	/*
		C-5
	 */
	@GetMapping("/{childId}/records/{recordId}")
	public String handleGetChildRecordView(
			@PathVariable("childId") Long childId,
			@PathVariable("recordId") Long recordId,
			Model model
	) {
		ChildRecordDTO childRecordDTO = childRecordService.findOneById(recordId);
		model.addAttribute("childRecord", childRecordDTO);
		return "children/childRecordView";
	}

	/*
		C-6
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
		C-7
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
       C-7
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
