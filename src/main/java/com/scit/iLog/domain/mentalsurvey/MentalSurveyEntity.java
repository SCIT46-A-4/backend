package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "mentalSurveys")
public class MentalSurveyEntity extends BaseTimeEntity {
    @Id
    private String id;
    private String title;
    private List<MentalSurveySection> sections;
    private LocalDateTime createdAt;
}