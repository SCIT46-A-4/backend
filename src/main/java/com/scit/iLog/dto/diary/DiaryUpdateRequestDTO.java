package com.scit.iLog.dto.diary;

public record DiaryUpdateRequestDTO(
        Long id,
        String title,
        String content
) {
}
