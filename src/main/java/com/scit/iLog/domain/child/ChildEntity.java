package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.HealthSurveyEntity;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.mentalsurvey.MentalSurveyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "child")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "child")
    private List<RelationShipEntity> relationShips = new ArrayList<>();

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child")
    @Builder.Default
    private List<ChildDiaryEntity> diaries = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child")
    @Builder.Default
    private List<ChildAssetEntity> assets = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child")
    @Builder.Default
    private List<HealthSurveyEntity> healthSurveys = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child")
    @Builder.Default
    private List<ChildRecordEntity> childRecords = new ArrayList<>();
}
