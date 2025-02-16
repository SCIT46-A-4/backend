package com.scit.iLog.dto.member;

import com.scit.iLog.domain.member.MemberRole;

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
    private String name;
    private String password;
    private String email;
    private MemberRole role;
}
