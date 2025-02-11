package com.scit.iLog.dto;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.domain.MemberRole;
import com.scit.iLog.domain.SupervisorRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long id;
    private String userId;
    private String name; // 추가
    private String password;
    private String email;
    private MemberRole role;
    private SupervisorRole supervisorRole; // ENUM 타입 변경

    public static MemberDTO toDTO(MemberEntity entity) {
        return MemberDTO.builder()
                .id(entity.getId())
                .name(entity.getName()) // 추가
                .userId(entity.getUserId())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}
