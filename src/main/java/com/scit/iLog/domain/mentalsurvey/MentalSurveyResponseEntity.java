package com.scit.iLog.domain.mentalsurvey;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(collection = "mentalSurveyResponses")
public class MentalSurveyResponseEntity {
    @Id
    private String id;
    private String surveyId;
    private String respondent;
    private List<SectionResponse> sectionResponses;
    private LocalDateTime createdAt;
    private double totalLikertScore;
}
