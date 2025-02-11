package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "child_asset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildAssetEntity extends BaseTimeEntity {
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
}
