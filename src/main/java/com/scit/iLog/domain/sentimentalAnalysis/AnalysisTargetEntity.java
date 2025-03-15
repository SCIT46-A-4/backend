package com.scit.iLog.domain.sentimentalAnalysis;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "analysis_target_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildEntity child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uploaded_by")
    private MemberEntity uploadedBy;

    @Builder.Default
    @OneToMany(mappedBy = "analysisTarget", cascade = CascadeType.ALL)
    private List<AnalysisTargetTypeEntity> analysisTargetTypes = new ArrayList<>();

    @Column(name = "original_target_file_name", length = 200)
    private String originalTargetFileName;

    @Column(name = "saved_target_file_name", length = 200)
    private String savedTargetFileName;

    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @OneToOne(mappedBy = "analysisTarget", cascade = CascadeType.ALL)
    private WeatherEntity weather;

    @Column(name = "companion")
    private String companion;

    @Column(name = "supplement", length = 1000)
    private String supplement;

    @Column(name = "analyzed_text")
    private String analyzedText;

    @OneToOne(mappedBy = "analysisTarget", fetch = LAZY, cascade = CascadeType.REMOVE)
    private AnalysisResultEntity analysisResult;

    @Column(name = "locationName")
    private String locationName;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public void replaceAllAnalysisTargetTypes(List<AnalysisTargetTypeEntity> analysisTargetTypes) {
        if (!ObjectUtils.isEmpty(analysisTargetTypes)) {
            this.analysisTargetTypes.clear();
            analysisTargetTypes.forEach(att -> att.setAnalysisTarget(this));
            this.analysisTargetTypes.addAll(analysisTargetTypes);
        }
    }
}
