package com.scit.iLog.dto.child;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildProfileDTO(
        Long childId,
        String name,
        LocalDateTime birthDate,
        String profileImgSrc,
        String birthLocation,
        String gender,
        String callName
) {
}
