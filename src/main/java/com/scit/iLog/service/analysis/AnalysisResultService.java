package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.*;
import com.scit.iLog.dto.analysis.*;
import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.weather.WeatherData;
import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import com.scit.iLog.repository.AnalysisResultRepository;
import com.scit.iLog.repository.AnalysisTargetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.scit.iLog.config.WebConfig.ANALYSIS_FILES_REQUEST_ROOT_PATH;

@Service
@RequiredArgsConstructor
public class AnalysisResultService {
	private final AnalysisResultRepository analysisResultRepository;
	private final AnalysisTargetRepository analysisTargetRepository;
	private final WeatherService weatherService;
	private final AnalysisClient fakeAnalysisClient;

	private AnalysisResultEntity findAnalysisResultById(Long analysisResultId) {
        return analysisResultRepository.findById(analysisResultId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("AnalysisResult 조회 실패: %d", analysisResultId)));
	}

	// 분석 결과 삭제
	@Transactional
	public boolean deleteAnalysisResult(Long analysisResultId) {
		if (analysisResultRepository.existsById(analysisResultId)) {
			analysisResultRepository.deleteById(analysisResultId);
			return true;
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Page<AnalysisResultListItemDTO> findAnalysisResults(Long childId, Pageable pageable) {
		return analysisTargetRepository.findAllByChild_Id(childId, pageable)
				.map(analysisTarget ->
						AnalysisResultListItemDTO.builder()
								.analysisTargetId(analysisTarget.getId())
								.analysisResultId(analysisTarget.getAnalysisResult().getId())
								.analysisResultTitle(analysisTarget.getAnalysisResult().getAnalysisResultText())
								.analysisDate(analysisTarget.getRegisterDate())
								.createdAt(analysisTarget.getAnalysisResult().getCreatedAt())
								.analysisTargetFileSrcUri(
										ANALYSIS_FILES_REQUEST_ROOT_PATH.concat(
												Normalizer.normalize(analysisTarget.getSavedTargetFileName(), Normalizer.Form.NFC)
										))
								.build()
				);
	}

	@Transactional(readOnly = true)
	public ChildEmotionRatioDataDTO getChildEmotionRatioDataBetween(Long childId, LocalDate startDate, LocalDate endDate) {
		if (!ObjectUtils.isEmpty(startDate) && !ObjectUtils.isEmpty(endDate)) {
			// 기간 내 데이터 조회 (시작 날짜와 종료 날짜 사이)
			List<AnalysisTargetEntity> analysisTargets = analysisTargetRepository
					.findByChildIdAndCreatedAtBetween(childId, startDate.atTime(0,0,0), endDate.atTime(23,59,59));
			List<AnalysisResultEntity> analysisResults = analysisTargets.stream()
					.map(analysisTarget -> analysisTarget.getAnalysisResult()).toList();
			// 전체 건수가 0이면 빈 리스트 반환
			long totalCount = analysisResults.size();
			if (totalCount == 0) {
				return new ChildEmotionRatioDataDTO(List.of());
			}

			// EmotionType 별 건수를 그룹화
			Map<EmotionType, Long> counts = analysisResults.stream()
					.collect(Collectors.groupingBy(AnalysisResultEntity::getEmotionType, Collectors.counting()));

			// 그룹화된 건수로 백분율 계산 후 DTO 리스트 생성
			List<ChildEmotionRatioStatPointDataDTO> ratios = new ArrayList<>();
			for (Map.Entry<EmotionType, Long> entry : counts.entrySet()) {
				int percentage = (int) Math.round(entry.getValue() * 100.0 / totalCount);
				ratios.add(new ChildEmotionRatioStatPointDataDTO(entry.getKey(), percentage, entry.getKey().getKoreanName()));
			}

			return new ChildEmotionRatioDataDTO(ratios);
		} else {
			// 파라미터가 하나라도 없으면 전체 데이터를 기본 간격인 최근 한달로 조회
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime dayAMonthAgo = today.minusMonths(1);
			// 기간 내 데이터 조회 (시작 날짜와 종료 날짜 사이)
			List<AnalysisResultEntity> analysisResults = analysisResultRepository.findAllByCreatedAtBetween(childId, dayAMonthAgo, today);
			// 전체 건수가 0이면 빈 리스트 반환
			long totalCount = analysisResults.size();
			if (totalCount == 0) {
				return new ChildEmotionRatioDataDTO(List.of());
			}

			// EmotionType 별 건수를 그룹화
			Map<EmotionType, Long> counts = analysisResults.stream()
					.collect(Collectors.groupingBy(AnalysisResultEntity::getEmotionType, Collectors.counting()));

			// 그룹화된 건수로 백분율 계산 후 DTO 리스트 생성
			List<ChildEmotionRatioStatPointDataDTO> ratios = new ArrayList<>();
			for (Map.Entry<EmotionType, Long> entry : counts.entrySet()) {
				int percentage = (int) Math.round(entry.getValue() * 100.0 / totalCount);
				ratios.add(new ChildEmotionRatioStatPointDataDTO(entry.getKey(), percentage, entry.getKey().getKoreanName()));
			}

			return new ChildEmotionRatioDataDTO(ratios);
		}
	}

	@Transactional
	public void updateAnalysisResultNote(Long analysisResultId, String content) {
		AnalysisResultEntity analysisResult = findAnalysisResultById(analysisResultId);
		if (ObjectUtils.isEmpty(analysisResult.getAnalysisResultNote())) {
			AnalysisResultNoteEntity analysisResultNote = AnalysisResultNoteEntity.builder()
					.analysisResult(analysisResult)
					.content(content)
					.build();
			analysisResult.setAnalysisResultNote(analysisResultNote);
		} else {
			AnalysisResultNoteEntity analysisResultNote = analysisResult.getAnalysisResultNote();
			analysisResultNote.setContent(content);
		}
	}

	@Transactional
	public void updateAnalysisResultTitle(Long analysisResultId, String title) {
		AnalysisResultEntity analysisResult = findAnalysisResultById(analysisResultId);
		analysisResult.setTitle(title);
	}

	@Transactional
	public void updateSatisfaction(Long analysisResultId, int score) {
		AnalysisResultEntity analysisResult = findAnalysisResultById(analysisResultId);
		if (ObjectUtils.isEmpty(analysisResult.getSatisfaction())) {
			AnalysisSatisfactionEntity analysisSatisfaction = AnalysisSatisfactionEntity.builder()
					.satisfactionScore(score)
					.analysisResult(analysisResult)
					.build();
			analysisResult.setSatisfaction(analysisSatisfaction);
		} else {
			analysisResult.getSatisfaction().setSatisfactionScore(score);
		}
	}

	@Transactional
	public void reAnalyze(Long analysisResultId, ReAnalyzeRequestDTO reAnalyzeRequest) {
		AnalysisResultEntity analysisResult = findAnalysisResultById(analysisResultId);
		AnalysisTargetEntity analysisTarget = analysisResult.getAnalysisTarget();
		analysisTarget.setRegisterDate(reAnalyzeRequest.datetime());

		WeatherResponse fakeWeatherResponse = weatherService.parseWeatherResponseFromJson(reAnalyzeRequest.weather());
		WeatherData weatherData = fakeWeatherResponse.data().get(0);
		LocalDateTime recordedAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(weatherData.dt()), ZoneId.systemDefault());
		String weatherDescription = weatherData.weather().get(0).description();
		
		WeatherEntity weather = analysisTarget.getWeather();
		weather.setHumidity(weatherData.humidity());
		weather.setTemperature(weatherData.temp());
		weather.setWindSpeed(weatherData.wind_speed());
		weather.setRecordedAt(recordedAt);
		weather.setWeatherDesc(weatherDescription);

		AIAnalysisResponseDTO aiAnalysisResponse = fakeAnalysisClient.getAIAnalysisResponse(analysisTarget);
		if (StringUtils.hasText(aiAnalysisResponse.extractedText())) analysisTarget.setAnalyzedText(aiAnalysisResponse.extractedText());

		analysisResult.setAnalysisResultText(aiAnalysisResponse.analysisResult());
		analysisResult.setSuggestedSolution(aiAnalysisResponse.suggestedSolution());
		analysisResult.setEmotionType(aiAnalysisResponse.emotionType());
		analysisResult.setEmotionScore(aiAnalysisResponse.emotionScore());
	}
}
