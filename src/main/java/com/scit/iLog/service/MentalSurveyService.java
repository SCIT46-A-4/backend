package com.scit.iLog.service;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.mentalsurvey.MentalSurveyEntity;
import com.scit.iLog.domain.mentalsurvey.MentalSurveyResponseEntity;
import com.scit.iLog.domain.mentalsurvey.QuestionResponse;
import com.scit.iLog.domain.mentalsurvey.SectionResponse;
import com.scit.iLog.dto.mentalsurvey.ChildMentalStatsDTO;
import com.scit.iLog.dto.mentalsurvey.ChildMentalSurveyStatPointDataDTO;
import com.scit.iLog.dto.mentalsurvey.response.*;
import com.scit.iLog.dto.mentalsurvey.survey.*;
import com.scit.iLog.repository.MentalSurveyRepository;
import com.scit.iLog.repository.MentalSurveyResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentalSurveyService {
    private final MentalSurveyRepository mentalSurveyRepository;
    private final MentalSurveyResponseRepository mentalSurveyResponseRepository;

    public MentalSurveyDetailsDTO getMetalSurveyDetails(String mentalSurveyId) {
        MentalSurveyEntity mentalSurvey = mentalSurveyRepository.findById(mentalSurveyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("MentalSurvey 조회 실패: %s", mentalSurveyId)));
        return new MentalSurveyDetailsDTO(
                mentalSurvey.getId(),
                mentalSurvey.getTitle(),
                mentalSurvey.getDescription(),
                mentalSurvey.getType(), // 2025-03-13 / 김은진 추가
                mentalSurvey.getSections().stream()
                        .map(mentalSurveySection -> new MentalSurveySectionDetailsDTO(
                                mentalSurveySection.getTitle(),
                                mentalSurveySection.getQuestions().stream()
                                        .map(mentalSurveyQuestion -> new MentalSurveyQuestionDetailsDTO(
                                                mentalSurveyQuestion
                                                        .getItem(),
                                                mentalSurveyQuestion
                                                        .getExample()))
                                        .toList()))
                        .toList(),
                mentalSurvey.getCreatedAt());
    }

    @Transactional
    public String saveMentalSurveyResponse(
            Long childId,
            Long memberId,
            RelationType relationType,
            String mentalSurveyId,
            MentalSurveyResponseInsertDTO mentalSurveyResponseInsertDTO
    ) {
        List<SectionResponseInsertDTO> sectionResponseInsertDTOS = mentalSurveyResponseInsertDTO.sectionResponses();
        int totalScore = sectionResponseInsertDTOS.stream()
                .mapToInt(section -> section.questionResponses().stream()
                        .mapToInt(QuestionResponseInsertDTO::score)
                        .sum())
                .sum();

        String resultComment = getResultComment(totalScore);
        List<SectionResponse> sectionResponses = sectionResponseInsertDTOS.stream()
                .map(sectionResponse ->
                        new SectionResponse(
                                sectionResponse.sectionTitle(),
                                sectionResponse.questionResponses().stream()
                                        .map(questionResponse ->
                                                new QuestionResponse(
                                                        questionResponse.questionItem(),
                                                        questionResponse.example(),
                                                        questionResponse.score())).toList(),
                                sectionResponse.questionResponses().stream()
                                        .mapToInt(QuestionResponseInsertDTO::score)
                                        .sum()
                        )).toList();
        MentalSurveyResponseEntity mentalSurveyResponse = MentalSurveyResponseEntity.builder()
                .surveyId(mentalSurveyId)
                .surveyTitle(mentalSurveyResponseInsertDTO.surveyTitle())
                .relationType(relationType.name())
                .childId(childId)
                .respondentId(memberId)
                .totalLikertScore(totalScore)
                .sectionResponses(sectionResponses)
                .comment(resultComment)
                .build();
        return mentalSurveyResponseRepository.save(mentalSurveyResponse).getId();
    }

    private String getResultComment(int totalScore) {
        if (totalScore < 15) {
            return "아동의 심리가 위험한 상태입니다!";
        } else if (totalScore < 30) {
            return "현재 주의 필요가 필요한 상태입니다!";
        } else if (totalScore < 45) {
            return "아이의 마음이 건강하네요!";
        } else {
            return "마음이 건강한 아이로 잘 자라고 있어요!";
        }
    }

    @Transactional(readOnly = true)
    public MentalSurveyResponseDetailsDTO getResponseDetailsById(Long childId, String responseId) {
        MentalSurveyResponseEntity mentalSurveyResponse = mentalSurveyResponseRepository.findById(responseId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("MentalSurveyResponse 조회 실패: %s", responseId)));
        List<SectionResponseDetailsDTO> sectionResponseDetails = mentalSurveyResponse.getSectionResponses().stream()
                .map(sectionResponse ->
                        new SectionResponseDetailsDTO(
                                sectionResponse.getSectionTitle(),
                                sectionResponse.getQuestionResponses().stream().map(
                                        questionResponse ->
                                                new QuestionResponseDetailsDTO(
                                                        questionResponse.getQuestionItem(),
                                                        questionResponse.getExample(),
                                                        questionResponse.getScore())
                                ).toList(),
                                sectionResponse.getQuestionResponses().stream()
                                        .mapToInt(QuestionResponse::getScore)
                                        .sum()
                        )).toList();
        return MentalSurveyResponseDetailsDTO.builder()
                .childId(childId)
                .totalLikertScore(mentalSurveyResponse.getTotalLikertScore())
                .sectionResponses(sectionResponseDetails)
                .comment(mentalSurveyResponse.getComment())
                .createdAt(mentalSurveyResponse.getCreatedAt())
                .responseId(mentalSurveyResponse.getId())
                .surveyTitle(mentalSurveyResponse.getSurveyTitle())
                .build();
    }

    @Transactional(readOnly = true)
    public ChildMentalStatsDTO getMentalSurveyStatsBetween(Long childId, Long memberId, LocalDate startDate, LocalDate endDate) {
        if (!ObjectUtils.isEmpty(startDate) && !ObjectUtils.isEmpty(endDate)) {
            // 기간 내 데이터 조회 (시작 날짜와 종료 날짜 사이)
            List<MentalSurveyResponseEntity> mentalSurveys = mentalSurveyResponseRepository
                    .findByChildCreatedAtBetween(childId, startDate.atTime(23, 59, 59), endDate.atTime(23, 59, 59));
            List<ChildMentalSurveyStatPointDataDTO> parentSurveyResponseStatPointData = mentalSurveys.stream()
                    .filter(mentalSurveyResponse -> mentalSurveyResponse.getRelationType().equals(RelationType.GUARDIAN.name()))
                    .map(mentalSurveyResponse ->
                            ChildMentalSurveyStatPointDataDTO.builder()
                                    .date(mentalSurveyResponse.getCreatedAt())
                                    .label(mentalSurveyResponse.getSurveyTitle())
                                    .resultScore(mentalSurveyResponse.getTotalLikertScore())
                                    .detailUrl(String.format("/children/%d/mentalSurveys/responses/%s/details", childId, mentalSurveyResponse.getId()))
                                    .build()).toList();
            List<ChildMentalSurveyStatPointDataDTO> teacherSurveyResponseStatPointData = mentalSurveys.stream()
                    .filter(mentalSurveyResponse -> mentalSurveyResponse.getRelationType().equals(RelationType.TEACHER.name()))
                    .map(mentalSurveyResponse ->
                            ChildMentalSurveyStatPointDataDTO.builder()
                                    .date(mentalSurveyResponse.getCreatedAt())
                                    .label(mentalSurveyResponse.getSurveyTitle())
                                    .resultScore(mentalSurveyResponse.getTotalLikertScore())
                                    .detailUrl(String.format("/children/%d/mentalSurveys/responses/%s/details", childId, mentalSurveyResponse.getId()))
                                    .build()).toList();
            return new ChildMentalStatsDTO(parentSurveyResponseStatPointData, teacherSurveyResponseStatPointData);
        } else {
            // 파라미터가 하나라도 없으면 전체 데이터를 기본 간격인 최근 한달로 조회
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime dayAMonthAgo = today.minusMonths(1);
            List<MentalSurveyResponseEntity> mentalSurveys = mentalSurveyResponseRepository
                    .findByChildCreatedAtBetween(childId, dayAMonthAgo, today);
            List<ChildMentalSurveyStatPointDataDTO> parentSurveyResponseStatPointData = mentalSurveys.stream()
                    .filter(mentalSurveyResponse -> mentalSurveyResponse.getRelationType().equals(RelationType.GUARDIAN.name()))
                    .map(mentalSurveyResponse ->
                            ChildMentalSurveyStatPointDataDTO.builder()
                                    .date(mentalSurveyResponse.getCreatedAt())
                                    .label(mentalSurveyResponse.getSurveyTitle())
                                    .resultScore(mentalSurveyResponse.getTotalLikertScore())
                                    .detailUrl(String.format("/children/%d/mentalSurveys/responses/%s/details", childId, mentalSurveyResponse.getId()))
                                    .build()).toList();
            List<ChildMentalSurveyStatPointDataDTO> teacherSurveyResponseStatPointData = mentalSurveys.stream()
                    .filter(mentalSurveyResponse -> mentalSurveyResponse.getRelationType().equals(RelationType.TEACHER.name()))
                    .map(mentalSurveyResponse ->
                            ChildMentalSurveyStatPointDataDTO.builder()
                                    .date(mentalSurveyResponse.getCreatedAt())
                                    .label(mentalSurveyResponse.getSurveyTitle())
                                    .resultScore(mentalSurveyResponse.getTotalLikertScore())
                                    .detailUrl(String.format("/children/%d/mentalSurveys/responses/%s/details", childId, mentalSurveyResponse.getId()))
                                    .build()).toList();
            return new ChildMentalStatsDTO(parentSurveyResponseStatPointData, teacherSurveyResponseStatPointData);
        }
    }

    public List<MentalSurveySelectInfoDTO> getMentalSurveySelectInfo(Long childId) {
        return mentalSurveyRepository.findAll()
                .stream()
                .map(mentalSurvey -> new MentalSurveySelectInfoDTO(
                        mentalSurvey.getTitle(),
                        mentalSurvey.getDescription(),
                        String.format("/children/%d/mentalSurveys/%s/responses/new", childId,
                                mentalSurvey.getId()),
                        mentalSurvey.getType())) // 2025-03-13 / 김은진 추가
                .toList();
    }

    /**
     * v1.x.x-10
     * getMentalScores
     * D-2 25/3/5 준성 MentalSurveyResponse 작업
     *
     * @param childId
     * @param startDate
     * @param endDate
     * @return
     */
    private List<MentalSurveyResponseChartDTO> getSurveyData(
            Long childId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Sort sort = Sort.by(asc("createdAt"));

        List<MentalSurveyResponseEntity> mentalSurveyResponseEntity
                = mentalSurveyResponseRepository.findAllByChildIdAndCreatedAtBetween(childId, startDate, endDate, sort);

        List<MentalSurveyResponseChartDTO> chartDtoList = new ArrayList<>();

        for (var data : mentalSurveyResponseEntity) {
            MentalSurveyResponseChartDTO _dto = MentalSurveyResponseChartDTO.builder()
                    .totalLikertScore(data.getTotalLikertScore())
                    .comment(data.getComment())
                    .surveyTitle(data.getSurveyTitle())
                    .createdAt(data.getCreatedAt())
                    .respondentId(data.getRespondentId())
                    .build();
            chartDtoList.add(_dto);
        }
        return chartDtoList;
    }

    // 1주일치 데이터 조회
    public List<MentalSurveyResponseChartDTO> getLastWeekData(Long childId) {
        return getSurveyData(childId, LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
    }

    // 1개월치 데이터 조회
    public List<MentalSurveyResponseChartDTO> getLastMonthData(Long childId) {
        return getSurveyData(childId, LocalDateTime.now().minusMonths(1), LocalDateTime.now());
    }

    // 1년치 데이터 조회
    public List<MentalSurveyResponseChartDTO> getLastYearData(Long childId) {
        return getSurveyData(childId, LocalDateTime.now().minusYears(1), LocalDateTime.now());
    }

    @Transactional
    public boolean deleteById(String responseId) {
        mentalSurveyResponseRepository.deleteById(responseId);
        return true;
    }
}
