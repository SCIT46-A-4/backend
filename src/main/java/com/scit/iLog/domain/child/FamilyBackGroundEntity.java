package com.scit.iLog.domain.child;

import com.scit.iLog.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "family_background")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyBackGroundEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_background_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_background")
    private FamilyBackGround familyBackGround;

    @OneToMany(mappedBy = "familyBackGround", fetch = FetchType.LAZY)
    public List<ChildBackGroundEntity> childBackGround;

    //이도훈 추가
    @Builder // 👈 추가
    public FamilyBackGroundEntity(FamilyBackGround familyBackGround) {
        this.familyBackGround = familyBackGround;
    }
}
