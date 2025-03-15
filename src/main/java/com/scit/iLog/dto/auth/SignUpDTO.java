package com.scit.iLog.dto.auth;

import com.scit.iLog.domain.RelationType;
import lombok.Builder;

@Builder
public record SignUpDTO(
        String userName,
        String signInId,
        String userPwd,
        String userEmail,
        RelationType relationType,
        //2025-02-17~20 이도훈 개인정보 수집 이용 동의
        boolean personalInformationCollectionAndUsageAgreement
) {
}
