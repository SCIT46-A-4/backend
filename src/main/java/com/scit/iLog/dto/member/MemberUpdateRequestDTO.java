package com.scit.iLog.dto.member;

public record MemberUpdateRequestDTO(
        String email,
        String userPwd
) {
}
