package com.scit.iLog.dto.analysis;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WritingAnalysisConfirmDTO(
        Long analysisTargetId,      // 분석 대상 식별자 (수정, 재분석 시 필요)
        String childName,           // "000 군" 등 아동 이름 표시
        String imagePreviewUrl,     // 등록한 글 이미지 미리보기 URL
        LocalDateTime targetDate,   // 일시 (수정 가능)
        String weather,             // 날씨 정보 (수정 가능)
        String companion,           // 함께 있었던 사람 (수정 가능)
        String supplement,          // 보충 설명 (수정 가능)
        String extractedText,       // 이미지에서 추출된 글 (수정 가능)
        String analysisResult       // 기존 글 분석 결과 (이미 존재한다면 표시)
) {
}
