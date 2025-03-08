package com.scit.iLog.domain.sentimentalAnalysis;


import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "analysis_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisTypeEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AnalysisType type;

    @OneToMany(mappedBy = "analysisType")
    private List<AnalysisTargetTypeEntity> analysisTargetTypes;

    private AnalysisTypeEntity(AnalysisType analysisType) {
        this.type = analysisType;
    }

    public static AnalysisTypeEntity create(AnalysisType analysisType) {
        return new AnalysisTypeEntity(analysisType);
    }
}
