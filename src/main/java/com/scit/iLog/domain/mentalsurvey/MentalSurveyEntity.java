package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "mental_survey")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentalSurveyEntity extends BaseTimeEntity {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mental_survey_id")
    private Long id;

    @Setter
    @Column(name = "title", length = 100)
    private String title;

    @Setter
    @Column(name = "description", length = 500)
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "mentalSurvey")
    private List<MentalQuestionEntity> questions = new ArrayList<>();
}