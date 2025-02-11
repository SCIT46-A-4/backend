package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "analysis_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResultEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_asset_id", nullable = false)
    private ChildAssetEntity childAsset;

    @Column(name = "analysis_result", length = 1000)
    private String analysisResult;

    @Column(name = "suggested_solution", length = 1000)
    private String suggestedSolution;

    @Column(name = "emotion_score")
    private double emotionScore;
}
