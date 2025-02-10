package com.scit.iLog.dto;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.domain.MemberRole;

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
    private String password;
    private String email;
    private MemberRole role; // USER, ADMIN
    private String supervisorRole; // parent, teacher, expert, carer
    
    public static MemberDTO toDTO(MemberEntity entity) {
        return MemberDTO.builder()
                .id(entity.getId())
                .userId(entity.getMemberId())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .role(entity.getRole()) // ENUM 타입 유지
                .build();
    }
}
