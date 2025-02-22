package com.scit.iLog.domain.sentimentalAnalysis;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "analysis_satisfaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisSatisfactionEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_satisfaction_id")
    private Long id;

    @Column(name = "satisfaction_score")
    private int satisfactionScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id")
    private AnalysisResultEntity analysisResult;
}
