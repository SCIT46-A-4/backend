package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import com.scit.iLog.dto.analysis.AnalysisResultListItemDTO;
import com.scit.iLog.dto.analysis.AnalysisResultNoteDetailsDTO;
import com.scit.iLog.dto.analysis.ImageAnalysisResultDetailsDTO;
import com.scit.iLog.repository.AnalysisResultRepository;
import com.scit.iLog.repository.AnalysisTargetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
