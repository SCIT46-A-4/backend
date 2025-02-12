package com.scit.iLog.dto.claims;

import com.scit.iLog.domain.claim.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimsInsertDTO {
    private Long id;
    private String title;
    private String content;
    private ClaimType type;
}