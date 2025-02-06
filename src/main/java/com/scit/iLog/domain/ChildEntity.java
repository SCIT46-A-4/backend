package com.scit.iLog.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "name")
    private String name;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(name = "weight")
    private double weight;

    @Column(name = "height")
    private double height;
    
    /*
     2025-02-06 이도훈 좌, 우 시력 엔티티 선언(테이블은 수정 안함)
     */
    @Column(name = "left_eye")
    private double leftEye;

    @Column(name = "right_eye")
    private double rightEye;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "child")
    @Builder.Default
    private List<ChildDiaryEntity> diaries = new ArrayList<>();
}
