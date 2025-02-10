package com.scit.iLog.domain;

import com.scit.iLog.dto.MemberDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "user_id", unique = true, nullable = false, length = 10)
    private String userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // realName → name으로 변경 (DB와 일치)

    @Column(name = "password", nullable = false, length = 65)
    private String password;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "supervisor_role", nullable = false)
    private SupervisorRole supervisorRole; // ENUM으로 선언

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    public static MemberEntity toEntity(MemberDTO dto) {
        return MemberEntity.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .name(dto.getName())  // realName이 아니라 name 사용
                .password(dto.getPassword())
                .email(dto.getEmail())
                .supervisorRole(dto.getSupervisorRole()) // 추가
                .role(dto.getRole())
                .build();
    }
}
