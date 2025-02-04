package com.scit.iLog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "relationship")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelationShip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relationship_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationShip")
    private Child child;

    @Enumerated(EnumType.STRING)
    private RelationType relationType;

}
