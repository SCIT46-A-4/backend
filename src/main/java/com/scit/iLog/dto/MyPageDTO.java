package com.scit.iLog.dto;

import lombok.Builder;

@Builder
public record MyPageDTO(
        String userName,
        String signInId,
        String userPwd,
        String userEmail,
        String relationType
) {
}
