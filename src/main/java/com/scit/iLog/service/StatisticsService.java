package com.scit.iLog.service;

import com.scit.iLog.domain.PeriodType;
import com.scit.iLog.dto.stats.*;
import com.scit.iLog.repository.AnalysisResultRepository;
import com.scit.iLog.repository.ChildRecordRepository;
import com.scit.iLog.repository.MentalSurveyResponseRepository;
import com.scit.iLog.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final ChildRecordRepository childRecordRepository;
    private final MentalSurveyResponseRepository mentalSurveyResponseRepository;
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

    /*
        특정 회원이 실시한 특정 아동의 심리 설문검사의
        총 합산 점수를 대표값으로 가지는 심리 설문 검사 통계용 DTO를 반환합니다.
     */
    @Transactional(readOnly = true)
    public ChildMentalStatsDTO getMentalStatsBy(
            Long childId,
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PeriodType periodType
    ) {
        List<ChildMentalSurveyStatPointDataDTO> mentalSurveyStatPointData = mentalSurveyResponseRepository.findByChildIdAndRespondentIdAndCreatedAtBetween(
                        childId, memberId, startDate, endDate
                )
                .stream()
                .map(mentalSurveyResponse ->
                        ChildMentalSurveyStatPointDataDTO.builder()
                                .date(mentalSurveyResponse.getCreatedAt())
                                .resultScore(mentalSurveyResponse.getTotalLikertScore())
                                .label(Double.toString(mentalSurveyResponse.getTotalLikertScore()))
                                .detailUrl(
                                        String.format(
                                                "/children/%d/mentalSurveys/results/%s",
                                                childId,
                                                mentalSurveyResponse.getId()
                                        )
                                )
                                .build()
                ).toList();
        return new ChildMentalStatsDTO(mentalSurveyStatPointData);
    }

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
}
