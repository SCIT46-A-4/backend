package com.scit.iLog.domain.healthCheck;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.child.ChildRecordEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.child.ChildEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "health_check")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthCheckEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_survey_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildEntity child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "child_record_id")
    private ChildRecordEntity childRecord;

    @Column(name = "original_file_name", length = 200)
    private String originalFileName;

    @Column(name = "saved_file_name", length = 200)
    private String savedFileName;
}
