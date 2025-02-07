package com.scit.iLog.dto.child;

import com.scit.iLog.domain.ChildEntity;

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
    public static ChildDetailsDto toDTO(ChildEntity childEntity) {
        return ChildDetailsDto.builder()
        		.id(childEntity.getId())
                .name(childEntity.getName())
                .weight(childEntity.getWeight())
                .height(childEntity.getHeight())
                .leftEye(childEntity.getLeftEye())
                .rightEye(childEntity.getRightEye())
                .build();
    }
}
