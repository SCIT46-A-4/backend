package com.scit.iLog.dto.member;

import com.scit.iLog.domain.member.MemberRole;
import lombok.*;

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
