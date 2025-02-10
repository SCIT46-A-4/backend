package com.scit.iLog.dto;

import com.scit.iLog.domain.ClaimsEntity;
import com.scit.iLog.domain.QuestionCategory;

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
    private String question;
    private String answer;
    private QuestionCategory category;
    
    public static ClaimsDTO toDTO(ClaimsEntity entity) {
        return ClaimsDTO.builder()
                .id(entity.getId())
                .authorId(entity.getAuthor().getId())  // MemberEntity에서 ID만 가져옴
                .title(entity.getTitle())
                .question(entity.getQuestion())
                .answer(entity.getAnswer())
                .category(entity.getCategory())
                .build();
    }
}