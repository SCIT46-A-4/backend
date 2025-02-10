package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "mental_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentalAnswerEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mental_answer_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mental_response_id", nullable = false)
    private MentalResponseEntity response;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mental_question_id", nullable = false)
    private MentalQuestionEntity mentalQuestion;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mental_option_id")
    private MentalOptionEntity option;

    @Column(name = "answer", length = 1000)
    private String answer;
}
