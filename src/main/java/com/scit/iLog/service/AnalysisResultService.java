package com.scit.iLog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisResultEntity;
import com.scit.iLog.repository.AnalysisResultRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalysisResultService {

	private final AnalysisResultRepository analysisResultRepository;

	// 특정 분석 결과 조회
	public AnalysisResultEntity getAnalysisResult(Long analysisId) {

		Optional<AnalysisResultEntity> result = analysisResultRepository.findById(analysisId);

		return null;
	}

	// 분석 결과 목록 조회
	public List<AnalysisResultEntity> getAllAnalysisResults() {

		return analysisResultRepository.findAll();
	}

	// 분석 결과 삭제
	@Transactional
	public void deleteAnalysisResult(Long analysisId) {

		Optional<AnalysisResultEntity> result = analysisResultRepository.findById(analysisId);

		// 존재하는지 확인
		if (result.isPresent()) {
			analysisResultRepository.delete(result.get());
		} else {
			throw new RuntimeException("분석 결과가 존재하지 않습니다.");
		}
	}
}
