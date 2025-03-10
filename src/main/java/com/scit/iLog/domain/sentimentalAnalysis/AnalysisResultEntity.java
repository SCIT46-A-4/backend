package com.scit.iLog.domain.sentimentalAnalysis;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

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
    @Column(name = "analysis_result_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "analysis_target_id", nullable = false)
    private AnalysisTargetEntity analysisTarget;

    @Column(name = "analysis_result_text", length = 1000)
    private String analysisResultText;

    @Column(name = "suggested_solution", length = 1000)
    private String suggestedSolution;

    @Column(name = "emotion_score")
    private double emotionScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type")
    private EmotionType emotionType;

    @Column(name = "title")
    private String title;

    @OneToOne(mappedBy = "analysisResult", fetch = LAZY, cascade = CascadeType.ALL)
    public AnalysisSatisfactionEntity satisfaction;

    @OneToOne(mappedBy = "analysisResult", fetch = LAZY, cascade = CascadeType.ALL)
    private AnalysisResultNoteEntity analysisResultNote;
}
