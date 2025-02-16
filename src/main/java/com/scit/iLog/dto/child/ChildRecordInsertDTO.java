package com.scit.iLog.dto.child;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Builder
public record ChildRecordInsertDTO(
        double height,
        double weight,
        double leftEye,
        double rightEye,
        String note,
        LocalDateTime registerDate,
        MultipartFile healthCheckImg
) {
}
