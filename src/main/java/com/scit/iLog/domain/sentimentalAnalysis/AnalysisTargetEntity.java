package com.scit.iLog.domain.sentimentalAnalysis;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "analysis_target")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisTargetEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_asset_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildEntity child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uploaded_by")
    private MemberEntity uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChildAssetType type;

    @Column(name = "original_survey_file_name", length = 200)
    private String originalSurveyFileName;

    @Column(name = "saved_survey_file_name", length = 200)
    private String savedSurveyFileName;

    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @OneToOne(mappedBy = "analysisTarget", cascade = CascadeType.ALL, orphanRemoval = true)
    private WeatherEntity weather;

    @Column(name = "companion")
    private String companion;

    @Column(name = "supplement", length = 1000)
    private String supplement;
}
