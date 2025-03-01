package com.scit.iLog.dto.child;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildRecordUpdateViewDTO(
        Long id,
        Long childId,    // 추가
        double weight,
        double height,
        double leftEye,
        double rightEye,
        String note,
        LocalDateTime registerDate,
        String healthCheckImageSrc
) {
}
