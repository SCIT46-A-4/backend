package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.claim.ClaimType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "mental_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentalQuestionEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mental_question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mental_survey_id", nullable = false)
    private MentalSurveyEntity mentalSurvey;

    @Column(name = "question_text", nullable = false, length = 200)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType type;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder.Default
    @OneToMany(mappedBy = "question")
    private List<MentalOptionEntity> options = new ArrayList<>();
}