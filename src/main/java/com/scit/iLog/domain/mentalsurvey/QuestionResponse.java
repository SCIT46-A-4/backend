package com.scit.iLog.domain.mentalsurvey;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class QuestionResponse {
    private String questionItem;
    private String answer;
    private int score;
}
