package com.scit.iLog.dto.claims;

import lombok.Builder;

import java.util.List;

@Builder
public record ClaimsAndAnswersDTO(
        int numberOfClaims,
        List<ClaimListViewDTO> claims
) {
}
