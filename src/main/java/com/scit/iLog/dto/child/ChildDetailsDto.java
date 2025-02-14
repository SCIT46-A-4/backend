package com.scit.iLog.dto.child;

import lombok.Builder;

/*
2025-02-06 이도훈 좌, 우 시력 엔티티 선언(테이블은 수정 안함)
*/

@Builder
public record ChildDetailsDto(
		Long id,
	    String name,
	    double weight,
	    double height,
	    double leftEye,
	    double rightEye
		) {
}
