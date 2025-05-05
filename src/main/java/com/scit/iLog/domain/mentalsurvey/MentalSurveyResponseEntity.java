package com.scit.iLog.domain.mentalsurvey;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(collection = "mentalSurveyResponses")
public class MentalSurveyResponseEntity {
    @Id
    private String id;
    private String surveyTitle;
    private String surveyId;
    private Long childId;
    private Long respondentId;
    private String relationType;
    private List<SectionResponse> sectionResponses;
    private int totalLikertScore;
    private String comment;
//    @CreatedDate
    private LocalDateTime createdAt;
//    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
