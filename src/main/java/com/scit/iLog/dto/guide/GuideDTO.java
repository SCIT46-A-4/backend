package com.scit.iLog.dto.guide;

import lombok.Builder;

@Builder
public record GuideDTO(
        Long id,
        String title,
        String content
) {
}