package com.scit.iLog.dto.member;

import com.scit.iLog.domain.RelationType;
import lombok.Builder;

@Builder
public record MyPageDTO(
        String userName,
        String signInId,
        String userPwd,
        String userEmail,
        RelationType relationType
) {
}
