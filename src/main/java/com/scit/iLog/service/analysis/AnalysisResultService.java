package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;
import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import com.scit.iLog.dto.analysis.*;
import com.scit.iLog.repository.AnalysisResultRepository;
import com.scit.iLog.repository.AnalysisTargetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scit.iLog.config.WebConfig.ANALYSIS_FILES_REQUEST_ROOT_PATH;

@Service
@RequiredArgsConstructor
public class AnalysisResultService {
	private final AnalysisResultRepository analysisResultRepository;
	private final AnalysisTargetRepository analysisTargetRepository;

	// 특정 분석 결과 조회
	@Transactional(readOnly = true)
	public ImageAnalysisResultDetailsDTO getAnalysisResultDetails(Long analysisResultId) {
		AnalysisResultEntity analysisResult = analysisResultRepository.findById(analysisResultId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("AnalysisResult 조회 실패: %d", analysisResultId)));
		return ImageAnalysisResultDetailsDTO.builder()
				.emotionScore(analysisResult.getEmotionScore())
				.emotionType(analysisResult.getEmotionType())
				.suggestedSolution(analysisResult.getSuggestedSolution())
				.analysisResultNote(new AnalysisResultNoteDetailsDTO(
								analysisResult.getAnalysisResultNote().getId(),
								analysisResult.getAnalysisResultNote().getContent())
				).build();
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
								.analysisResultTitle(analysisTarget.getAnalysisResult().getAnalysisResult())
								.analysisDate(analysisTarget.getRegisterDate())
								.analysisTargetFileSrcUri(
										ANALYSIS_FILES_REQUEST_ROOT_PATH
												.concat(analysisTarget.getSavedTargetFileName()))
								.build()
				);
	}

	@Transactional(readOnly = true)
	public ChildEmotionRatioDataDTO getChildEmotionRatioDataBetween(Long childId, LocalDate startDate, LocalDate endDate) {
		if (!ObjectUtils.isEmpty(startDate) && !ObjectUtils.isEmpty(endDate)) {
			// 기간 내 데이터 조회 (시작 날짜와 종료 날짜 사이)
//			List<AnalysisResultEntity> analysisResults = analysisResultRepository
//					.findAllByCreatedAtBetween(childId, startDate.atStartOfDay(), endDate.atStartOfDay());
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
			List<AnalysisResultEntity> analysisResults = analysisResultRepository
					.findAllByCreatedAtBetween(childId, dayAMonthAgo, today);
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
}
