package com.scit.iLog.dto.member;

import com.scit.iLog.domain.RelationType;
import lombok.Builder;

@Builder
public record MemberSelectDTO(
        Long id,
        String signInId,
        String name,
        String email,
        RelationType relationType
) {
}
