package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;
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
    public static final String DEFAULT_PROFILE_IMG = "default-profile.png";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    /*
     * @TODO embedded 엔티티로 바꿔야함
     */
    @Column(name = "birth_location")
    private String birthLocation;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "callname")
    private String callName;

    @Column(name = "original_profile_img_name")
    private String originalProfileImgName;

    @Column(name = "saved_profile_img_name")
    private String savedProfileImgName;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<ChildDiaryEntity> diaries = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<AnalysisTargetEntity> analysisTargets = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<ChildRecordEntity> childRecords = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    private List<RelationShipEntity> relationShips = new ArrayList<>();

    // 2025-02-28 / 김은진 / 가정환경 데이터 저장
    @BatchSize(size = 100)
    @Builder.Default
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildBackGroundEntity> childBackGrounds = new ArrayList<>();

    public void replaceAllChildBackGrounds(List<ChildBackGroundEntity> newBackGrounds) {
        if (newBackGrounds != null) {
            this.childBackGrounds.clear();
            newBackGrounds.forEach(bg -> bg.setChild(this));
            this.childBackGrounds.addAll(newBackGrounds);
        }
    }

    public void deleteAllChildBackground() {
        this.childBackGrounds.clear();
    }

    public void setDefaultProfileImg() {
        this.setSavedProfileImgName(DEFAULT_PROFILE_IMG);
        this.setOriginalProfileImgName(null);
    }
}
