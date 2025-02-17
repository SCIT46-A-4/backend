package com.scit.iLog.domain.mentalsurvey;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MentalSurveyQuestion {
    private String item;
    private String example;
}
