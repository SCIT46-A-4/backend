package com.scit.iLog.domain;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "health_survey")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthSurvey extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @Column(name = "survey_data")
    private String data;

}
