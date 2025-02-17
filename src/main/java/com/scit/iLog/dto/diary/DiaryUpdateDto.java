package com.scit.iLog.dto.diary;

import lombok.Builder;

import java.time.LocalDateTime;

/*
2025-02-10 이도훈
DiaryController의 handleGetDiaryUpdateView메서드에서 diaryId를 찾기 위한 Dto와 빌더.
*/
/*
	@TODO 파라미터가 2개밖에 안되면 빌더 쓰는 거 아닙니다.
 */
@Builder
public record DiaryUpdateDto(
		Long id,
		String title,
	    String content,
		LocalDateTime createdAt
) {
}
