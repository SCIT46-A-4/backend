package com.scit.iLog.controller;

import com.scit.iLog.domain.PeriodType;
import com.scit.iLog.dto.stats.ChildEmotionStatsDTO;
import com.scit.iLog.dto.stats.ChildPhysicalStatsDTO;
import com.scit.iLog.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 *
 */
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    /**
     * "우리 아이 일기 감정 분석 그래프" 페이지를 보여주는 뷰를 반환하는 핸들러
     *
     * @return children/statistics/statisticsListView.html 뷰 페이지
     *
     * ST-1
     */
    @GetMapping("/statisticsDetails")
    public String handleGetStatisticsDetailsView() {
        return "/children/statistics/detailsView";
    }

    @GetMapping("/{childId}/physicalStats")
    public ChildPhysicalStatsDTO handleGetChildPhysicalStats(
            @PathVariable("childId") Long childId,
            @RequestParam("startDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDateTime startDate,
            @RequestParam(value = "endDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDateTime endDate,
            @RequestParam("periodType") PeriodType periodType
            ) {
        return statisticsService.getPhysicalInfoBy(childId,startDate,endDate,periodType);
    }

//    @GetMapping("/{childId}/mentalStats")
//    public ChildMentalStatsDTO handleGetChildMentalStats(
//            @PathVariable("childId") Long childId,
//            @RequestParam("startDate")
//            @DateTimeFormat(pattern = "yyyy-MM-dd")
//            LocalDateTime startDate,
//            @RequestParam(value = "endDate")
//            @DateTimeFormat(pattern = "yyyy-MM-dd")
//            LocalDateTime endDate,
//            @RequestParam("periodType") PeriodType periodType,
//            @AuthenticationPrincipal MemberDetails memberDetails
//            ) {
//        return statisticsService.getMentalStatsBy(childId, memberDetails.getId(), startDate,endDate,periodType);
//    }

    @GetMapping("/{childId}/emotionStats")
    public ChildEmotionStatsDTO handleGetChildEmotionStats(
            @PathVariable("childId") Long childId,
            @RequestParam("startDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDateTime startDate,
            @RequestParam(value = "endDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDateTime endDate,
            @RequestParam("periodType") PeriodType periodType
            ) {
        return statisticsService.getEmotionStatsBy(childId,startDate,endDate,periodType);
    }
    
    
    //-------------------------------------------------------------------------------------------------
    /**
     * 2025-03-05~07 이도훈
     * v1.x.x-13
     * D-2 
     * @param childId
     * @param startDate
     * @param endDate
     * @param periodType
     * @return
     */
    @ResponseBody
    @GetMapping("/{childId}/dashBoardEmotionStats")
    public ChildEmotionStatsDTO handleGetDashBoardChildEmotionStats(
            @PathVariable("childId") Long childId,
            @RequestParam("startDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") //시작일 년월일로 지정
            LocalDateTime startDate,
            @RequestParam(value = "endDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") //종료일 년월일로 지정
            LocalDateTime endDate,
            @RequestParam("periodType") PeriodType periodType
    		) {
    	
    	return statisticsService.getDashBoardEmotionStatsBy(childId, startDate, endDate, periodType);
    
    }
}
