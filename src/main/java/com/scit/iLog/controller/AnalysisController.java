package com.scit.iLog.controller;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisType;
import com.scit.iLog.dto.PageResponse;
import com.scit.iLog.dto.analysis.*;
import com.scit.iLog.dto.child.ChildRecordListItemDTO;
import com.scit.iLog.dto.mentalsurvey.ChildMentalStatsDTO;
import com.scit.iLog.service.analysis.AnalysisResultService;
import com.scit.iLog.service.analysis.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/children/{childId}/analysis")
public class AnalysisController {
    private final AnalysisResultService analysisResultService;
    private final AnalysisService analysisService;

    // 분석 결과 목록 조회 | 분석결과 리스트를 조회해서 페이지 반환하는 메서드
    /*
        AI-1: (1)이냐(2)냐 보기의 전환은 자바스크립트로 처리합니다.
     */
    @GetMapping("/resultList")
    public String handleGetAnalysisResultListView() {
        // 모든 결과를 조회하고 데이터를 담는다 분석 결과 리스트 페이지 반환
        return "children/analysis/analysisResultListView";
    }

    /*
        AI-1 에서 이것을 호출해서 데이터를 받아오면 됩니다.
     */
    @ResponseBody
    @GetMapping("/results")
    public PageResponse<AnalysisResultListItemDTO> handleGetAnalysisResultList(
            @PathVariable("childId") Long childId,
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
        Page<AnalysisResultListItemDTO> analysisResultsPage = analysisResultService.findAnalysisResults(childId, pageable);

        return PageResponse.<AnalysisResultListItemDTO>builder()
                .content(analysisResultsPage.getContent())
                .pageNumber(analysisResultsPage.getNumber())
                .pageSize(analysisResultsPage.getSize())
                .totalElements(analysisResultsPage.getTotalElements())
                .totalPages(analysisResultsPage.getTotalPages())
                .last(analysisResultsPage.isLast())
                .build();
    }

    // 그림,사진,음성 분석 페이지 요청
    /*
        AI-2
     */
    @GetMapping("/new")
    public String handleGetAnalysisInsertView() {
        // 25/2/7 준성 : 우리 아이 그림, 사진, 음성 분석 페이지 반환
        return "/children/analysis/analysisInsertView";
    }

    /*
        AI-2
     */
    @PostMapping("/new")
    public String handlePostAnalysisTargetInsertRequest(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("childId") Long childId,
            @ModelAttribute AnalysisTargetInsertDTO analysisTargetInsertDTO,
            RedirectAttributes redirectAttributes
    ) {
        Long analysisTargetId = analysisService.saveAnalysisTarget(memberDetails.getId(), childId, analysisTargetInsertDTO);
        if (analysisTargetInsertDTO.analysisTypes().contains(AnalysisType.WRITING)) {
            TextExtractionDTO textExtraction = analysisService.getExtractedText(analysisTargetId);
            analysisService.saveTextExtraction(analysisTargetId, textExtraction.text());
            redirectAttributes.addFlashAttribute("textExtraction", textExtraction);
            return String.format("redirect:/children/%d/analysis/%d/review", childId, analysisTargetId);
        }
        Long analysisResultId = analysisService.getImageAnalysisResult(analysisTargetId);
        return String.format("redirect:/children/%d/analysis/results/%d", childId, analysisResultId);
    }

    /*
        AI-3
        그림, 사진 분석 결과 조회 및 수정 페이지
     */
    @GetMapping("/results/{analysisResultId}")
    public String handleGetAnalysisResultRequest(
            @PathVariable("analysisResultId") Long analysisResultId,
            Model model
    ) {
        ImageAnalysisResultDetailsDTO resultDetails = analysisService.getImageAnalysisResultDetails(analysisResultId);
        model.addAttribute("analysisResult", resultDetails);
        return "children/analysis/analysisResultDetailsView";
    }

    /*
        AI-3
        분석결과에 대한 노트 저장하기,
     */
    @ResponseBody
    @PostMapping("/results/{analysisResultId}/analysisResultNotes/new")
    public boolean handlePostAnalysisResultNoteInsertRequest(
            @ModelAttribute AnalysisResultNoteInsertDTO analysisResultNoteInsertDTO,
            @PathVariable("analysisResultId") Long analysisResultId
    ) {
        analysisService.saveAnalysisResultNote(analysisResultId, analysisResultNoteInsertDTO);
        return true;
    }

    /*
        @TODO 분석 결과 만족도 저장 및 수정 기능 추가
     */

    /*
        @TODO 분석 결과 제목 저장 및 수정하기 기능 추가
     */

    /*
        AI-3
     */
    // 분석 결과 삭제
    @ResponseBody
    @DeleteMapping("/results/{analysisResultId}")
    public boolean handleDeleteAnalysisResult(
            @PathVariable("analysisResultId") Long analysisResultId,
            RedirectAttributes redirectAttributes
    ) {
        /*
            @TODO 리다이렉트를 프론트엔드에서 jquery로 처리해야합니다.
         */
        return analysisResultService.deleteAnalysisResult(analysisResultId);
    }

    /*
        AI-4
        분석 결과 검토
     */
    @GetMapping("/{analysisTargetId}/review")
    public String handleGetTextExtraction(
            @PathVariable("analysisTargetId") Long analysisTargetId,
            @ModelAttribute TextExtractionDTO textExtractionDTO,
            Model model
    ) {
        model.addAttribute("textExtraction", textExtractionDTO);
        return "children/analysis/analysisResultReviewView";
    }

    /*
        AI-4
        분석 결과 검토 페이지에서 "분석 진행하기" 누를시 데이터 전송 및 분석
     */
    @PostMapping("/{analysisTargetId}/review")
    public String handlePostWritingAnalysisRequest(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("childId") Long childId,
            @PathVariable("analysisTargetId") Long analysisTargetId,
            @ModelAttribute WritingAnalysisTargetInsertDTO writingAnalysisTargetInsertDTO
    ) {
        analysisService.updateAnalyzedTextOfAnalysisTarget(analysisTargetId, writingAnalysisTargetInsertDTO);
        Long analysisResultId = analysisService.getWritingAnalysisResult(analysisTargetId);
        return String.format("redirect:/children/%d/analysis/results/%d", childId, analysisResultId);
    }

    /*
        @TODO 재분석 기능 추가 필요.
     */

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @GetMapping("/results/stats/emotion/data")
    public ChildEmotionRatioDataDTO handleGetAnalysisStatsData(
            @PathVariable("childId") Long childId,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate
    ) {
        // 데이터베이스에서 시간순으로 정렬된 데이터 조회
        return analysisResultService.getChildEmotionRatioDataBetween(childId, startDate, endDate);
    }
}
