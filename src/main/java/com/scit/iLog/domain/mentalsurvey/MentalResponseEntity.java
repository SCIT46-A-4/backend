package com.scit.iLog.domain.mentalsurvey;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "mental_response")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentalResponseEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mental_response_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private MentalSurveyEntity survey;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity author;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "response")
    private List<MentalAnswerEntity> answers = new ArrayList<>();
}
