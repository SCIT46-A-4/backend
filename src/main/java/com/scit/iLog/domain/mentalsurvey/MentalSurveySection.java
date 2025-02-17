package com.scit.iLog.domain.mentalsurvey;

import jakarta.persistence.Transient;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MentalSurveySection {
    private String title;
    @Transient
    private List<MentalSurveyQuestion> questions;
}
