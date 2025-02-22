package com.scit.iLog.dto.diary;

import lombok.Builder;

/*
2025-02-10 이도훈
DiaryController의 handleGetDiaryUpdateView메서드에서 diaryId를 찾기 위한 Dto와 빌더.
*/
@Builder
public record DiaryCreateDto(String content, Long author) {

}
