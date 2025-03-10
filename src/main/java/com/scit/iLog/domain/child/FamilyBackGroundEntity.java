package com.scit.iLog.domain.child;

import java.util.List;

import com.scit.iLog.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
