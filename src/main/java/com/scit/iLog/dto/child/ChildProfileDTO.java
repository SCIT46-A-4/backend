package com.scit.iLog.dto.child;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChildProfileDTO(
        Long id,
        String name,
        LocalDateTime birthDate,
        String profileImgSrc
) {
}
