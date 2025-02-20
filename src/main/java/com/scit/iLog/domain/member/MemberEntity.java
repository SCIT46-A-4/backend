package com.scit.iLog.domain.member;

import java.util.ArrayList;
import java.util.List;

import com.scit.iLog.domain.BaseTimeEntity;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.RelationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sign_in_id", nullable = false, length = 100, unique = true)
    private String signInId;

    @Column(name = "password", nullable = false, length = 65)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private MemberRole role = MemberRole.USER;

  //2025-02-17~20 이도훈 @Enumerated(EnumType.STRING)추가
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type")
    private RelationType relationType;
    
    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<RelationShipEntity> relationShips = new ArrayList<>();
    
    //2025-02-17~20 이도훈 개인정보 수집 이용 동의
    @Column(name="personal_information_collection_and_usage_agreement", nullable = false)
    private boolean personalInformationCollectionAndUsageAgreement;
}
