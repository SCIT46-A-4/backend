package com.scit.iLog.controller;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.dto.PageResponse;
import com.scit.iLog.dto.child.*;
import com.scit.iLog.service.analysis.AnalysisService;
import com.scit.iLog.service.child.ChildRecordService;
import com.scit.iLog.service.child.ChildService;
import com.scit.iLog.util.FilePathUtil;
import com.scit.iLog.util.PageNavigator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            @AuthenticationPrincipal MemberDetails memberDetails,
            RedirectAttributes redirectAttributes
    ) {
        Long childId = childService.saveBasicInfo(memberDetails.getId(), childBasicInfoInsertDTO);
        redirectAttributes.addAttribute("childId", childId);
        log.info("childId: {}", childId.toString());
        return new ChildBasicInfoInsertResponseDTO(childId);
    }

    /**
     * C-2
     * 특정 ID에 해당하는 아동 기본 정보를 조회하여 "아동 기본 정보 조회 페이지" 뷰에 전달하는 핸들러
     *
     * @param childId
     * @param model
     * @return children/basicInfoDetailsView.html 뷰 페이지
     * 서비스 레이어에서 ChildDetailsDto를 가져와 뷰에 전달
     */
    @GetMapping("/{childId}/basicInfo")
    public String handleGetChildBasicInfoView(
            @PathVariable("childId") Long childId,
            Model model
    ) {
        // ID에 해당하는 아동 기본 정보를 조회
        ChildBasicInfoDetailsDTO childBasicInfo = childService.getBasicInfoById(childId);
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
    @ResponseBody
    @DeleteMapping("/{childId}")
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
        ChildBasicInfoDetailsDTO childBasicInfo = childService.getBasicInfoById(childId);
        log.debug("수정 페이지 로딩 - 가정환경 데이터: {}", childBasicInfo.getFamilyBackGrounds());
        model.addAttribute("childBasicInfo", childBasicInfo);
        return "children/basicInfoUpdateView";
    }

    /*
     * C-3
     * 25/2/11 준 api-30 아이 정보 수정
     */
    @ResponseBody
    @PostMapping("/{childId}/basicInfo/edit")
    public boolean handlePostChildBasicInfoUpdate(
            @PathVariable("childId") Long childId,
            @ModelAttribute ChildBasicInfoUpdateDTO updateDTO
    ) {
        log.info("수정 요청 - 가정환경 데이터: {}", updateDTO.familyBackGrounds());
        childService.updateChildBasicInfo(childId, updateDTO);
        return true;
    }

    /**
     * 2025-02-25 / 김보경
     * c-2 페이지 바로가기 버튼을 작성하다 수정
     * PathVariable 및 model 추가
     * 아동 상세 정보 등록 페이지를 조회
     * 25/2/11 준 : api-24 아이 상제정보 등록페이지 반환
     *
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
    @ResponseBody
    @PostMapping("/{childId}/records/new")
    public ChildRecordResponseDTO handlePostChildRecordInsert(
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
     *
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
        ChildRecordDetailsDTO childRecordDetailsDTO = childRecordService.findDetailsById(recordId);
        model.addAttribute("childId", childId);
        model.addAttribute("childRecord", childRecordDetailsDTO);
        return "children/records/childRecordDetailsView";
    }

    /**
     * API : v1.x.x-3
     * C-6 이도훈 2025-02-24~26
     * http://localhost:9900/children/1/recordList
     * offset → page 변경
     *
     * @param childId 아이 ID
     * @param model   모델
     * @return 신체 기록 목록 뷰
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{childId}/recordList")
    public String handleGetChildRecordListView(
            @PathVariable("childId") Long childId,
            @PageableDefault(size = 5, sort = "registerDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        // 아이의 기본 정보와 페이징된 레코드 조회
        ChildRecordListBasicInfoDTO childName = childService.findBasicInfoById(childId);
        Page<ChildRecordListItemDTO> childRecordsPage = childRecordService.findPagedChildRecords(childId, pageable);

        // 페이지 네비게이터 생성 (예: 그룹당 5페이지씩)
        PageNavigator pageNavigator = PageNavigator.of()
                .groupSize(5)
                .itemsPerPage(childRecordsPage.getSize())
                .currentPage(childRecordsPage.getNumber())
                .totalPages(childRecordsPage.getTotalPages())
                .build();

        // 페이지 응답 DTO 구성
        PageResponse<ChildRecordListItemDTO> pageResponse = PageResponse.<ChildRecordListItemDTO>builder()
                .content(childRecordsPage.getContent())
                .pageNumber(childRecordsPage.getNumber())
                .pageSize(childRecordsPage.getSize())
                .totalElements(childRecordsPage.getTotalElements())
                .totalPages(childRecordsPage.getTotalPages())
                .last(childRecordsPage.isLast())
                .build();

        // 정렬 정보 추출 (첫 번째 정렬 기준 사용)
        Sort.Order order = pageable.getSort().iterator().next();

        // 모델에 데이터 추가
        model.addAttribute("childRecordsPage", pageResponse);
        model.addAttribute("childName", childName);
        model.addAttribute("pageNavigator", pageNavigator);
        model.addAttribute("sortBy", order.getProperty());
        model.addAttribute("direction", order.getDirection().name());

        return "children/records/childRecordListView";
    }

    /**
     * API : v1.x.x-4 아이의 기록 삭제
     * C-6 이도훈 2025-02-24~26
     *
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
     *
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
        ChildRecordUpdateViewDTO childRecordUpdateInfo = childRecordService.getChildRecordUpdateInfoByRecordId(childRecordId);
        model.addAttribute("childRecord", childRecordUpdateInfo);
        return "children/records/childRecordUpdateView";
    }

    /**
     * API : v1.x.x-9
     * C-7 이도훈
     *
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
            @AuthenticationPrincipal MemberDetails memberDetails,
            @ModelAttribute ChildRecordUpdateRequestDTO childRecordUpdateRequestDTO
    ) {
        childRecordService.updateChildRecord(childId, memberDetails.getId(), childRecordId, childRecordUpdateRequestDTO);
        return new ChildRecordUpdateResponseDTO(true, String.format("/children/%d/records/%d", childId, childRecordId));
    }
}
