package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "mental_survey")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentalSurveyEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mental_survey_id")
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;
}