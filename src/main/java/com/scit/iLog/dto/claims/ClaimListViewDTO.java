package com.scit.iLog.dto.claims;

import lombok.Builder;
/*
   이렇게 dto에는 반드시 dto만 포함해야합니다. - 호준
*/
@Builder
public record ClaimListViewDTO(
        Long claimId,
        String authorName,
        String title,
        String content,
        String type,
        ClaimAnswerDTO claimAnswerDTO
) {
}
