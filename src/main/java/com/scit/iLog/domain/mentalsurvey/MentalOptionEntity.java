package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "mental_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentalOptionEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mental_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mental_question_id", nullable = false)
    private MentalQuestionEntity question;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "option_text", nullable = false, length = 200)
    private String optionText;
}
