package com.scit.iLog.dto.child;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildRecordDetailsDTO(
        Long childRecordId,
        double weight,
        double height,
        double leftEye,
        double rightEye,
        String note,
        LocalDateTime registerDate,
        HealthCheckImageDTO healthCheckImage
) {
}
