package com.scit.iLog.dto.claims;

import lombok.Builder;

import java.time.LocalDateTime;

/*
   이렇게 dto에는 반드시 dto만 포함해야합니다. - 호준
*/
@Builder
public record ClaimListViewDTO(
        String type,
        Long claimId,
        String title,
        LocalDateTime createdDate, // 문의 날짜 추가
        int answerCount  // ✅ 추가: 답변 개수
) {
    public boolean hasAnswers() {
        return answerCount > 0;
    }
}
