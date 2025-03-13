package com.scit.iLog.dto.member;

import lombok.Builder;

@Builder
public record MemberUpdateDTO(
        String name,
        String signInId,
        String email,
        String relationType,
        boolean personalInformationCollectionAndUsageAgreement
) {
}
