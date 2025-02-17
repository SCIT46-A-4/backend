package com.scit.iLog.domain.mentalsurvey;

import jakarta.persistence.Transient;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Document(collection = "mentalSurveys")
public class MentalSurveyEntity {
    @Id
    private String id;
    private String title;
    @Transient
    private List<MentalSurveySection> sections;
    private LocalDateTime createdAt;
}