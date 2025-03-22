package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

// child에 대한 자세한 정보들
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

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;

    @Column(name = "left_eye")
    private Double leftEye;

    @Column(name = "right_eye")
    private Double rightEye;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "register_date")
    @CreationTimestamp
    private LocalDateTime registerDate;

    @OneToOne(mappedBy = "childRecord", fetch = LAZY, cascade = CascadeType.REMOVE)
    private HealthCheckEntity healthCheck;

    public String getDescription() {
        return String.format("등록 날짜: %s, 키: %f, 몸무게: %f, 왼쪽시력: %f, 오른쪽시력: %f, 진단소견: %s",
                this.getCreatedAt().toString(), this.height, this.weight, this.leftEye, this.rightEye, this.note);
    }
}
