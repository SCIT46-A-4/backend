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
@Table(name = "child_background")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildBackGroundEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_background_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "child_id")
    private ChildEntity child;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "family_background_id")
    private FamilyBackGroundEntity familyBackGround;
}
