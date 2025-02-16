package com.scit.iLog.domain.mentalsurvey;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MentalSurveySection {
    private String title;
    private List<MentalSurveyQuestion> questions;
}
