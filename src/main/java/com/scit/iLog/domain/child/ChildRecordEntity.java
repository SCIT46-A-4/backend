package com.scit.iLog.domain.child;

import static jakarta.persistence.FetchType.LAZY;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.healthCheck.HealthCheckEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
