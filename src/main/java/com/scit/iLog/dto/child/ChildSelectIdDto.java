package com.scit.iLog.dto.child;

import lombok.Builder;


/**
 * 2025-02-11~13이도훈
 * 아이의 정보. id와 name, authId
 */
@Builder
public record ChildSelectIdDto(
		Long id,
	    String name,
	    String authId
		) {
}
