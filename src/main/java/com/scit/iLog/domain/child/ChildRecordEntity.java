package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "child_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildRecordEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_record_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildEntity child;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "note", length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;

    @Column(name = "left_eye")
    private Double leftEye;

    @Column(name = "right_eye")
    private Double rightEye;

    @Column(name = "original_photo_name", length = 100)
    private String originalPhotoName;

    @Column(name = "saved_photo_name", length = 100)
    private String savedPhotoName;
}
