package com.scit.iLog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;
import com.scit.iLog.domain.PeriodType;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import com.scit.iLog.dto.analysis.AnalysisResult;
import com.scit.iLog.dto.stats.ChildEmotionStatPointDataDTO;
import com.scit.iLog.dto.stats.ChildEmotionStatsDTO;
import com.scit.iLog.dto.stats.ChildPhysicalStatPointDataDTO;
import com.scit.iLog.dto.stats.ChildPhysicalStatsDTO;
import com.scit.iLog.repository.AnalysisResultRepository;
import com.scit.iLog.repository.ChildRecordRepository;
import com.scit.iLog.util.DateTimeUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final ChildRecordRepository childRecordRepository;
//    private final MentalSurveyResponseRepository mentalSurveyResponseRepository;
    private final AnalysisResultRepository analysisResultRepository;

    @Transactional(readOnly = true)
    public ChildPhysicalStatsDTO getPhysicalInfoBy(
            Long childId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PeriodType periodType
    ) {
        List<ChildPhysicalStatPointDataDTO> physicalStatPointData = childRecordRepository
                .findAllByCreatedAtBetween(childId, startDate, endDate)
                .stream()
                .map(childRecord ->
                        ChildPhysicalStatPointDataDTO.builder()
                                .registerDate(childRecord.getRegisterDate())
                                .note(childRecord.getNote())
                                .height(childRecord.getHeight())
                                .weight(childRecord.getWeight())
                                .leftEye(childRecord.getLeftEye())
                                .rightEye(childRecord.getRightEye())
                                .label(childRecord.getRegisterDate().format(DateTimeUtil.DATE_FORMATTER))
                                .detailUrl(
                                        String.format(
                                                "/children/%d/records/%d",
                                                childRecord.getChild().getId(),
                                                childRecord.getId())
                                ).build()).toList();
        return new ChildPhysicalStatsDTO(physicalStatPointData);
    }

//    /*
//        특정 회원이 실시한 특정 아동의 심리 설문검사의
//        총 합산 점수를 대표값으로 가지는 심리 설문 검사 통계용 DTO를 반환합니다.
//     */
//    @Transactional(readOnly = true)
//    public ChildMentalStatsDTO getMentalStatsBy(
//            Long childId,
//            Long memberId,
//            LocalDateTime startDate,
//            LocalDateTime endDate,
//            PeriodType periodType
//    ) {
//        List<ChildMentalSurveyStatPointDataDTO> mentalSurveyStatPointData = mentalSurveyResponseRepository.findByChildIdAndRespondentIdAndCreatedAtBetween(
//                        childId, memberId, startDate, endDate
//                )
//                .stream()
//                .map(mentalSurveyResponse ->
//                        ChildMentalSurveyStatPointDataDTO.builder()
//                                .date(mentalSurveyResponse.getCreatedAt())
//                                .resultScore(mentalSurveyResponse.getTotalLikertScore())
//                                .label(Double.toString(mentalSurveyResponse.getTotalLikertScore()))
//                                .detailUrl(
//                                        String.format(
//                                                "/children/%d/mentalSurveys/results/%s",
//                                                childId,
//                                                mentalSurveyResponse.getId()
//                                        )
//                                )
//                                .build()
//                ).toList();
//        return new ChildMentalStatsDTO(mentalSurveyStatPointData);
//    }

    @Transactional(readOnly = true)
    public ChildEmotionStatsDTO getEmotionStatsBy(
            Long childId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PeriodType periodType
    ) {
        List<ChildEmotionStatPointDataDTO> emotionStatPointData = analysisResultRepository.findAllByCreatedAtBetween(childId, startDate, endDate)
                .stream()
                .map(analysisResult ->
                        ChildEmotionStatPointDataDTO.builder()
                                .date(analysisResult.getAnalysisTarget().getRegisterDate())
                                .resultScore(analysisResult.getEmotionScore())
                                .label(Double.toString(analysisResult.getEmotionScore()))
                                .detailUrl(
                                        String.format(
                                                "/children/%d/analysis/results?emotionType=%s",
                                                childId,
                                                analysisResult.getEmotionType().toString())
                                ).build()).toList();
        return new ChildEmotionStatsDTO(emotionStatPointData);
    }

    //---------------------------------------------------------------------------------------------------
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
	public ChildEmotionStatsDTO getDashBoardEmotionStatsBy(
			Long childId,
			LocalDateTime startDate, 
			LocalDateTime endDate,
			PeriodType periodType
			) {
		
		// startDate나 endDate가 null이면 기본값 (1주일)을 적용
	    LocalDateTime now = LocalDateTime.now();
	    if (startDate == null) {
	        startDate = now.minusDays(7);
	    }
	    if (endDate == null) {
	        endDate = now;
	    }

	    // 해당 기간 동안의 모든 분석 결과를 조회
	    List<AnalysisResultEntity> results = analysisResultRepository.findAllByCreatedAtBetween(childId, startDate, endDate);
	    long totalCount = results.size();

	    Map<EmotionType, Long> countByEmotion = results.stream()
	            .collect(Collectors.groupingBy(AnalysisResultEntity::getEmotionType, Collectors.counting()));
	    

	    // 각 감정 타입별 비율(퍼센티지)을 계산하여 DTO로 매핑
	    List<ChildEmotionStatPointDataDTO> dashBoardEmotionStatPointData = countByEmotion.entrySet().stream()
	            .map(entry -> {
	                double percentage = totalCount > 0 ? (entry.getValue() * 100.0 / totalCount) : 0.0;
	                String detailUrl = String.format(
	                        "/%d/analysis/results?emotionType=%s",
	                        childId,
	                        entry.getKey().toString());
	                return ChildEmotionStatPointDataDTO.builder()
	                        // Pie Chart에서는 날짜 정보가 필요 없으므로 null 처리
	                        .date(null)
	                        .resultScore(percentage)
	                        // label에는 감정의 한국어 이름 사용
	                        .label(entry.getKey().getKoreanName())
	                        .detailUrl(detailUrl)
	                        .build();
	            })
	            .collect(Collectors.toList());

	    System.out.println("================================================");
	    System.out.println(dashBoardEmotionStatPointData);
	    return new ChildEmotionStatsDTO(dashBoardEmotionStatPointData);
	}
}
