package com.scit.iLog.dto.auth;

import lombok.Builder;

@Builder
public record SignUpDTO(
        String userName,
        String signInId,
        String userPwd,
        String userEmail,
        String relationType
) {
}
