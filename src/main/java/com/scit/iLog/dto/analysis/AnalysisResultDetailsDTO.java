package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalysisResultDetailsDTO(
        Long analysisResultId,                 // 분석 결과 식별자 (AJAX 요청 등에 활용)
        String analysisResultTitle,            // 제목 (유니크 식별자)
        Long childId,
        String childName,                      // 아동 이름 (예: "000 군" 대체)
        String targetFileSrc,                  // 등록된 파일(이미지) 미리보기 경로
        LocalDateTime targetDateTime,          // 일시 (재분석 시 수정 가능)
        String locationName,                       // 위치 (재분석 시 수정 가능)
        double latitude,
        double longitude,
        String weather,                        // 날씨 (재분석 시 수정 가능)
        String companion,                      // 함께 있었던 사람 (재분석 시 수정 가능)
        String supplement,                     // 데이터에 대한 보충 설명 (재분석 시 수정 가능)
        String extractedText,                  // 이미지에서 추출된 텍스트 (수정 가능)
        double emotionScore,                   // 감정 점수 (읽기 전용)
        String emotionDescription,               // 감정 타입 (읽기 전용)
        String analysisResult,                 // 분석 결과 (읽기 전용)
        String suggestedSolution,              // 솔루션 (읽기 전용)
        AnalysisResultNoteDetailsViewDTO analysisResultNote,               // 분석 결과에 대한 메모
        AnalysisResultSatisfactionDetailsViewDTO analysisResultSatisfaction // 만족도 점수
) {
}
