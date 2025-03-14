package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.dto.analysis.AnalysisResultDetailsDTO;
import com.scit.iLog.dto.analysis.AnalysisResultNoteDetailsViewDTO;
import com.scit.iLog.dto.analysis.AnalysisResultSatisfactionDetailsViewDTO;
import com.scit.iLog.dto.analysis.AnalysisTargetInsertDTO;
import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.weather.WeatherData;
import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import com.scit.iLog.exception.MemberNotFoundException;
import com.scit.iLog.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.scit.iLog.config.WebConfig.ANALYSIS_FILES_REQUEST_ROOT_PATH;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private static final String childDataExtractionPrompt =
            "please extract height, weight, leftEye, rightEye, diagnosis of this child from this img";
    private final AnalysisClient fakeAnalysisClient;
    private final WeatherService weatherService;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisTargetRepository analysisTargetRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final AnalysisTypeRepository analysisTypeRepository;

    @Transactional
    public Long getAnalysisResult(Long analysisTargetId) {
        AnalysisTargetEntity analysisTarget = findAnalysisTargetById(analysisTargetId);
        AIAnalysisResponseDTO aiAnalysisResponse = fakeAnalysisClient.getAIAnalysisResponse(analysisTarget);
        if (StringUtils.hasText(aiAnalysisResponse.extractedText())) analysisTarget.setAnalyzedText(aiAnalysisResponse.extractedText());

        AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                .title("Analysis-".concat(UUID.randomUUID().toString()))
                .analysisTarget(analysisTarget)
                .analysisResultText(aiAnalysisResponse.analysisResult())
                .suggestedSolution(aiAnalysisResponse.suggestedSolution())
                .emotionType(aiAnalysisResponse.emotionType())
                .emotionScore(aiAnalysisResponse.emotionScore())
                .build();

        AnalysisResultNoteEntity analysisResultNote = AnalysisResultNoteEntity.builder()
                .analysisResult(analysisResult)
                .content("")
                .build();
        AnalysisSatisfactionEntity analysisSatisfaction = AnalysisSatisfactionEntity.builder()
                .analysisResult(analysisResult)
                .satisfactionScore(0)
                .build();
        analysisResult.setAnalysisResultNote(analysisResultNote);
        analysisResult.setSatisfaction(analysisSatisfaction);
        analysisResultRepository.save(analysisResult);

        return analysisResult.getId();
    }

    @Transactional(readOnly = true)
    public AnalysisResultDetailsDTO getAnalysisResultDetails(Long analysisTargetId) {
        AnalysisTargetEntity analysisTarget = findAnalysisTargetById(analysisTargetId);
        AnalysisResultEntity analysisResult = analysisTarget.getAnalysisResult();
        return AnalysisResultDetailsDTO.builder()
                .analysisResultId(analysisResult.getId())
                .analysisResultTitle(analysisResult.getTitle())
                .childId(analysisTarget.getChild().getId())
                .childName(analysisTarget.getChild().getName())
                .targetFileSrc(ANALYSIS_FILES_REQUEST_ROOT_PATH.concat(analysisTarget.getSavedTargetFileName()))
                .targetDateTime(analysisTarget.getRegisterDate())
                .locationName(analysisTarget.getLocationName())
                .latitude(analysisTarget.getLatitude())
                .longitude(analysisTarget.getLongitude())
                .weather(analysisTarget.getWeather().getWeatherDesc())
                .companion(analysisTarget.getCompanion())
                .supplement(analysisTarget.getSupplement())
                .analysisTypes(
                        analysisTarget.getAnalysisTargetTypes().stream()
                                .map(analysisTargetType ->
                                        analysisTargetType.getAnalysisType().getType()).toList())
                .extractedText(analysisTarget.getAnalyzedText())
                .emotionScore(analysisResult.getEmotionScore())
                .emotionDescription(analysisResult.getEmotionType().getKoreanName())
                .analysisResult(analysisResult.getAnalysisResultText())
                .suggestedSolution(analysisResult.getSuggestedSolution())
                .analysisResultNote(
                        new AnalysisResultNoteDetailsViewDTO(analysisResult.getAnalysisResultNote().getId(),analysisResult.getAnalysisResultNote().getContent()))
                .analysisResultSatisfaction(
                        new AnalysisResultSatisfactionDetailsViewDTO(analysisResult.getSatisfaction().getId(),analysisResult.getSatisfaction().getSatisfactionScore()))
                .build();
    }

    private AnalysisTargetEntity findAnalysisTargetById(Long analysisTargetId) {
        return analysisTargetRepository.findById(analysisTargetId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("AnalysisTarget 조회 실패: %d", analysisTargetId)));
    }

    @Transactional
    public Long saveAnalysisTarget(Long memberId, Long childId, AnalysisTargetInsertDTO analysisTargetInsertDTO) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member 조회 실패: ".concat(memberId.toString())));
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child 조회 실패: ".concat(childId.toString())));
        WeatherResponse fakeWeatherResponse = weatherService.getFakeWeatherResponse();
        AnalysisTargetEntity analysisTarget = AnalysisTargetEntity.builder()
                .locationName(analysisTargetInsertDTO.locationName())
                .latitude(analysisTargetInsertDTO.latitude())
                .longitude(analysisTargetInsertDTO.longitude())
                .registerDate(analysisTargetInsertDTO.targetDate())
                .companion(analysisTargetInsertDTO.companion())
                .uploadedBy(member)
                .supplement(analysisTargetInsertDTO.supplementaryComment())
                .child(child)
                .originalTargetFileName("test-target.png")
                .savedTargetFileName("test-target.png").build();

        if (!ObjectUtils.isEmpty(analysisTargetInsertDTO.analysisTypes()) && !analysisTargetInsertDTO.analysisTypes().isEmpty()) {
            List<AnalysisTargetTypeEntity> analysisTargetTypes = analysisTypeRepository.findAll()
                    .stream()
                    .filter(analysisType ->
                            analysisTargetInsertDTO.analysisTypes().contains(analysisType.getType()))
                    .map(analysisType -> AnalysisTargetTypeEntity.builder()
                            .analysisTarget(analysisTarget)
                            .analysisType(analysisType)
                            .build())
                    .toList();

            analysisTarget.replaceAllAnalysisTargetTypes(analysisTargetTypes);
        }

        WeatherData weatherData = fakeWeatherResponse.data().get(0);
        LocalDateTime recordedAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(weatherData.dt()), ZoneId.systemDefault());
        String weatherDescription = weatherData.weather().get(0).description();
        WeatherEntity weather = WeatherEntity.builder()
                .analysisTarget(analysisTarget)
                .humidity(weatherData.humidity())
                .temperature(weatherData.temp())
                .windSpeed(weatherData.wind_speed())
                .analysisTarget(analysisTarget)
                .recordedAt(recordedAt)
                .weatherDesc(weatherDescription)
                .build();
        analysisTarget.setWeather(weather);
        return analysisTargetRepository.save(analysisTarget).getId();
    }

    public ChildRecordExtraction getExtractChildRecordData(MultipartFile healthCheckImg) {
        return new FakeAnalysisClient.FakeChildRecordExtraction();
    }

    @Transactional
    public void inValidateByMember(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        analysisTargetRepository.findAllByUploadedBy(member)
                .forEach(analysisTarget ->
                        analysisTarget.setUploadedBy(null));
    }
}
