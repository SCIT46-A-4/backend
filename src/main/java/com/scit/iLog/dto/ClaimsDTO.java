package com.scit.iLog.dto;

import com.scit.iLog.domain.claim.ClaimEntity;
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
public class ClaimsDTO {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private ClaimType type;
    
    public static ClaimsDTO toDTO(ClaimEntity entity) {
        return ClaimsDTO.builder()
                .id(entity.getId())
                .authorId(entity.getAuthor().getId())  // MemberEntity에서 ID만 가져옴
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .build();
    }
}