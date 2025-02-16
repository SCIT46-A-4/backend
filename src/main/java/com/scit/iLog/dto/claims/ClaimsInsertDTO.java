package com.scit.iLog.dto.claims;

import com.scit.iLog.domain.claim.ClaimType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimsInsertDTO {
    private String title;
    private String content;
    private ClaimType type;
}