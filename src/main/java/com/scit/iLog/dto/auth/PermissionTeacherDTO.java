package com.scit.iLog.dto.auth;

import java.time.LocalDateTime;

import javax.management.relation.RelationType;

import com.scit.iLog.domain.permition.PermissionRequestStatus;

import lombok.Builder;

@Builder
public record PermissionTeacherDTO(

        Long id,                // id
        String alias,
        String guardianName,    // 보호자 이름
        String inviteeName,     // 초대된 사람 이름
        String childName,       // 아이이름
        RelationType relation,        // 관계
        PermissionRequestStatus permissionRequestStatus, // 현재 상태
        LocalDateTime approvalDate // 승인완료된 시간

) {
}