package com.scit.iLog.dto;

import com.scit.iLog.domain.RelationType;

public record MemberDashboardProfileDTO(
        String memberName,
        RelationType relationType
) {
}
