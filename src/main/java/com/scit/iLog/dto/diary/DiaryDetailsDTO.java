package com.scit.iLog.dto.diary;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DiaryDetailsDTO(
        String title,
        String content,
        LocalDateTime createdAt
) {
}
