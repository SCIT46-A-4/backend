package com.scit.iLog.domain;

import com.scit.iLog.dto.MemberDTO;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private MemberRole role;

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<RelationShipEntity> relationShips = new ArrayList<>();

    public static MemberEntity toEntity(MemberDTO dto) {
        return MemberEntity.builder()
                .id(dto.getId())
                .signInId(dto.getUserId())
                .name(dto.getName())  // realName이 아니라 name 사용
                .password(dto.getPassword())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }
}
