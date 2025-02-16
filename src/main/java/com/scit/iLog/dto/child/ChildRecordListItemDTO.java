package com.scit.iLog.dto.child;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildRecordListItemDTO(
        Long id,
        double weight,
        double height,
        double leftEye,
        double rightEye,
        String note,
        LocalDateTime registerDate,
        boolean hasHealthCheckImg
) {
}
