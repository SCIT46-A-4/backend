package com.scit.iLog.dto.member;

import com.scit.iLog.domain.RelationType;
import lombok.Builder;

@Builder
public record MemberUpdateDTO(
        String name,
        String signInId,
        String userPwd,
        String email,
        String relationType,
        boolean personalInformationCollectionAndUsageAgreement
) {
}
