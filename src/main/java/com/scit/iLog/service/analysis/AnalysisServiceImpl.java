package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.dto.analysis.*;
import com.scit.iLog.dto.analysis.ai.AiAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.ai.AiAnalysisResponseForWritingDTO;
import com.scit.iLog.dto.analysis.weather.WeatherData;
import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import com.scit.iLog.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private static final String childDataExtractionPrompt =
            "please extract height, weight, leftEye, rightEye, diagnosis of this child from this img";
    private final AnalysisClient fakeAnalysisClient;
    private final WeatherService weatherService;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisTargetRepository analysisTargetRepository;
    private final MemberRepository memberRepository;
    private final ChildRepository childRepository;
    private final AnalysisResultNoteRepository analysisResultNoteRepository;
    private final AnalysisSatisfactionRepository analysisSatisfactionRepository;

    @Override
    public ChildRecordExtraction getExtractChildRecordData(MultipartFile healthCheckImg) {
        return fakeAnalysisClient.getChildRecordData(
                childDataExtractionPrompt,
                healthCheckImg
        );
    }

    /*
        AnalysisResultEntity의 id를 반환
     */
    @Transactional
    @Override
    public Long getWritingAnalysisResult(Long analysisTargetId) {
        AnalysisTargetEntity analysisTarget = analysisTargetRepository.findById(analysisTargetId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "AnalysisTarget 조회 실패: ".concat(analysisTargetId.toString())));
        AiAnalysisResponseForWritingDTO aiAnalysisResponse = fakeAnalysisClient.getAiAnalysisResponseForWritings();
        AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                .analysisTarget(analysisTarget)
                .suggestedSolution(aiAnalysisResponse.suggestedSolution())
                .emotionType(aiAnalysisResponse.emotionType())
                .emotionScore(aiAnalysisResponse.emotionScore())
                .build();
        analysisResultRepository.save(analysisResult);
        return analysisResult.getId();
    }

    /*
        AnalysisResultEntity의 id를 반환
     */
    @Transactional(readOnly = true)
    @Override
    public Long getImageAnalysisResult(Long analysisTargetId) {
        AnalysisTargetEntity analysisTarget = analysisTargetRepository.findById(analysisTargetId)
                .orElseThrow(() -> new EntityNotFoundException(
                "AnalysisTarget 조회 실패: ".concat(analysisTargetId.toString())));
        AiAnalysisResponseDTO aiAnalysisResponse = fakeAnalysisClient.getAiAnalysisResponse();
        AnalysisResultEntity analysisResult = AnalysisResultEntity.builder()
                .analysisTarget(analysisTarget)
                .suggestedSolution(aiAnalysisResponse.suggestedSolution())
                .emotionType(aiAnalysisResponse.emotionType())
                .emotionScore(aiAnalysisResponse.emotionScore())
                .build();
        analysisResultRepository.save(analysisResult);
        return analysisResult.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public ImageAnalysisResultDetailsDTO getImageAnalysisResultDetails(Long analysisResultId) {
        AnalysisResultEntity analysisResult = analysisResultRepository.findById(analysisResultId)
                .orElseThrow(() -> new EntityNotFoundException("AnalysisResult 조회실패".concat(analysisResultId.toString())));
        AnalysisResultNoteEntity analysisResultNote = analysisResult.getAnalysisResultNote();
        AnalysisSatisfactionEntity satisfaction = analysisResult.getSatisfaction();
        return ImageAnalysisResultDetailsDTO.builder()
                .emotionType(analysisResult.getEmotionType())
                .suggestedSolution(analysisResult.getSuggestedSolution())
                .emotionScore(analysisResult.getEmotionScore())
                .analysisResultNote(
                        ObjectUtils.isEmpty(analysisResultNote) ?
                        new AnalysisResultNoteDetailsDTO(
                                -1L,
                                "") :
                                new AnalysisResultNoteDetailsDTO(
                                        analysisResultNote.getId(),
                                        analysisResultNote.getContent())
                )
                .analysisResultSatisfaction(
                    new AnalysisResultSatisfactionDetailsDTO(satisfaction.getId(),satisfaction.getSatisfactionScore()))
                .build();
    }

    @Transactional
    @Override
    public void saveTextExtraction(Long analysisTargetId, String text) {
        AnalysisTargetEntity analysisTarget = analysisTargetRepository.findById(analysisTargetId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("AnalysisTarget 조회 실패: %d", analysisTargetId)));
        analysisTarget.setAnalyzedText(text);
    }

    @Transactional
    @Override
    public void updateAnalyzedTextOfAnalysisTarget(Long analysisTargetId, WritingAnalysisTargetInsertDTO writingAnalysisTargetInsertDTO) {
        AnalysisTargetEntity analysisTarget = analysisTargetRepository.findById(analysisTargetId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("AnalysisTarget 조회 실패: %d", analysisTargetId)));
        analysisTarget.setAnalyzedText(writingAnalysisTargetInsertDTO.analysisTargetText());
    }

    @Transactional
    @Override
    public Long saveAnalysisTarget(Long memberId, Long childId, AnalysisTargetInsertDTO analysisTargetInsertDTO) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member 조회 실패: ".concat(memberId.toString())));
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child 조회 실패: ".concat(childId.toString())));
        //@TODO 가짜 날씨 클라이언트 만들어야함.
        WeatherResponse fakeWeatherResponse = weatherService.getWeatherResponseFrom(analysisTargetInsertDTO.weatherResponse());
        AnalysisTargetEntity analysisTarget = AnalysisTargetEntity
                .builder()
                .type(AnalysisType.PHOTO)
                .registerDate(analysisTargetInsertDTO.targetDate())
                .companion(analysisTargetInsertDTO.companion())
                .uploadedBy(member)
                .supplement(analysisTargetInsertDTO.supplementaryComment())
                .child(child)
                .originalTargetFileName("child_photo.jpeg")
                .savedTargetFileName("child_photo_123.jpeg")
                .build();
        WeatherData weatherData = fakeWeatherResponse.data().get(0);
        LocalDateTime recordedAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(weatherData.dt()), ZoneId.systemDefault());
        String weatherDescription = weatherData.weather().get(0)
                .description();
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

    @Transactional
    @Override
    public void saveAnalysisResultNote(Long analysisResultId, AnalysisResultNoteInsertDTO analysisResultNoteInsertDTO) {
        AnalysisResultEntity analysisResult = analysisResultRepository.findById(analysisResultId)
                .orElseThrow(() -> new EntityNotFoundException("AnalysisResult 조회 실패: ".concat(analysisResultId.toString())));

        AnalysisResultNoteEntity analysisResultNote = AnalysisResultNoteEntity.builder()
                .analysisResult(analysisResult)
                .content("좋은 분석입니다.")
                .build();
        AnalysisSatisfactionEntity analysisSatisfaction = AnalysisSatisfactionEntity.builder()
                .satisfactionScore(3)
                .analysisResult(analysisResult)
                .build();
        analysisResultNoteRepository.save(analysisResultNote);
        analysisSatisfactionRepository.save(analysisSatisfaction);
    }

    @Override
    public TextExtractionDTO getExtractedText(Long analysisTargetId) {
        return new TextExtractionDTO("추출 결과");
    }
}
