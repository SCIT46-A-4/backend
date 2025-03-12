package com.scit.iLog.dto.auth;

import java.time.LocalDateTime;

import com.scit.iLog.domain.RelationType;

import lombok.Builder;

@Builder
public record PermissionTeacherDTO(
                Long id, // id
                String childName, // 아이이름
                LocalDateTime birthDate,       // 아이 생년월일
                String guardianName, // 보호자 이름                                
                LocalDateTime approvalDate, // 승인완료된 시간
                RelationType relation // 관계
) {
}