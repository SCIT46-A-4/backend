package com.scit.iLog.domain.mentalsurvey;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SectionResponse {
    private String sectionTitle;
    private List<QuestionResponse> questionResponses;
    private int sectionLikertScore;
}
