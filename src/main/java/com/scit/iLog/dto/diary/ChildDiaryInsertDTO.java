package com.scit.iLog.dto.diary;

public record ChildDiaryInsertDTO(
        Long childId,
        String title,
        String content
) {
}
