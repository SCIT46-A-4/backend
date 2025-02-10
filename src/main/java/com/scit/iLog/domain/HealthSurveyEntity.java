package com.scit.iLog.domain;

import com.scit.iLog.domain.child.ChildEntity;
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
public class HealthSurveyEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_survey_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildEntity child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "original_survey_file_name", length = 200)
    private String originalSurveyFileName;

    @Column(name = "saved_survey_file_name", length = 200)
    private String savedSurveyFileName;
}
