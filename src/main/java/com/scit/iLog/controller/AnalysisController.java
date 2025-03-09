package com.scit.iLog.controller;

import com.scit.iLog.config.SecurityConfig.MemberDetails;
import com.scit.iLog.dto.PageResponse;
import com.scit.iLog.dto.analysis.*;
import com.scit.iLog.service.analysis.AnalysisResultService;
import com.scit.iLog.service.analysis.AnalysisService;
import com.scit.iLog.service.child.ChildService;
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
    private final ChildService childService;

    // 분석 결과 목록 조회 | 분석결과 리스트를 조회해서 페이지 반환하는 메서드
    /*
        AI-1: (1)이냐(2)냐 보기의 전환은 자바스크립트로 처리합니다.
     */
    @GetMapping("/resultList")
    public String handleGetAnalysisResultListView(
            @PathVariable("childId") Long childId,
            Model model
    ) {
        String childName = childService.getChildNameById(childId);
        model.addAttribute("childId", childId);
        model.addAttribute("childName", childName);
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
    public String handleGetAnalysisInsertView(
            @PathVariable("childId") Long childId,
            Model model
    ) {
        model.addAttribute("childId", childId);
        // 25/2/7 준성 : 우리 아이 그림, 사진, 음성 분석 페이지 반환
        return "/children/analysis/analysisInsertView";
    }

    /*
        AI-2
     */
    @ResponseBody
    @PostMapping("/new")
    public AnalysisTargetInsertResponseDTO handlePostAnalysisTargetInsertRequest(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("childId") Long childId,
            @ModelAttribute AnalysisTargetInsertDTO analysisTargetInsertDTO
    ) {
        Long analysisTargetId = analysisService.saveAnalysisTarget(memberDetails.getId(), childId, analysisTargetInsertDTO);
        Long analysisResultId = analysisService.getAnalysisResult(analysisTargetId);
        return new AnalysisTargetInsertResponseDTO(
                true,
                String.format("/children/%d/analysis/%d/results/%d/details", childId, analysisTargetId, analysisResultId));
    }

    /*
        AI-3
        그림, 사진 분석 결과 조회 및 수정 페이지
        수정은 Ajax로 처리
     */
    @GetMapping("/{analysisTargetId}/results/{analysisResultId}/details")
    public String handleGetAnalysisResultRequest(
            @PathVariable("analysisTargetId") Long analysisTargetId,
            @PathVariable("analysisResultId") Long analysisResultId,
            Model model
    ) {
        AnalysisResultDetailsDTO analysisResultDetails =  analysisService.getAnalysisResultDetails(analysisTargetId);
        model.addAttribute("analysisResultDetails", analysisResultDetails);
        return "children/analysis/analysisResultDetailsView";
    }

    /*
        AI-3
     */
    @ResponseBody
    @PutMapping("/{analysisTargetId}/results/{analysisResultId}/title")
    public boolean handlePostUpdateAnalysisResultTitle(
            @PathVariable Long analysisResultId,
            @RequestParam("title") String title
    ) {
        analysisResultService.updateAnalysisResultTitle(analysisResultId, title);
        return true;
    }

    /*
        AI-3
     */
    @ResponseBody
    @PutMapping("/{analysisTargetId}/results/{analysisResultId}/note")
    public boolean handlePostUpdateAnalysisResultNote(
            @PathVariable("analysisResultId") Long analysisResultId,
            @RequestParam("content") String content
    ) {
        analysisResultService.updateAnalysisResultNote(analysisResultId, content);
        return true;
    }

    /*
        AI-3
     */
    @ResponseBody
    @PutMapping("/{analysisTargetId}/results/{analysisResultId}/satisfaction")
    public boolean handlePostUpdateAnalysisResultSatisfaction(
            @PathVariable Long analysisResultId,
            @RequestParam("score") int score
    ) {
        analysisResultService.updateSatisfaction(analysisResultId, score);
        return true;
    }

    /*
        AI-3
     */
    @ResponseBody
    @PostMapping("/{analysisTargetId}/results/{analysisResultId}/reanalyze")
    public boolean handlePostReAnalyzeRequest(
            @PathVariable Long analysisResultId,
            @ModelAttribute ReAnalyzeRequestDTO reAnalyzeRequest
    ) {
        analysisResultService.reAnalyze(analysisResultId, reAnalyzeRequest);
        return true;
    }

    /*
        AI-3
     */
    @ResponseBody
    @DeleteMapping("/results/{analysisResultId}")
    public boolean handleDeleteAnalysisResult(
            @PathVariable("analysisResultId") Long analysisResultId
    ) {
        return analysisResultService.deleteAnalysisResult(analysisResultId);
    }

    /*
        D-1
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
