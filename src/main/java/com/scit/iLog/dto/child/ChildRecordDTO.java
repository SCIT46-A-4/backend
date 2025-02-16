package com.scit.iLog.dto.child;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildRecordDTO(
    LocalDateTime registerDate,
    double weight,
    double height,
    double leftEye,
    double rightEye,
    String note,
    String healthCheckImageSrc
) {
}
