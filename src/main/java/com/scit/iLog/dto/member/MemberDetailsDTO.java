package com.scit.iLog.dto.member;

import com.scit.iLog.domain.RelationType;
import lombok.Builder;

@Builder
public record MemberDetailsDTO(
        String signInId,
        String name,
        String email,
        String relationType,
        boolean personalInformationCollectionAndUsageAgreement
) {
}
