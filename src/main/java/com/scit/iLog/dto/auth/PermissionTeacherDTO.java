package com.scit.iLog.dto.auth;

import java.time.LocalDateTime;

import javax.management.relation.RelationType;

import com.scit.iLog.domain.permition.PermissionRequestStatus;

import lombok.Builder;

@Builder
public record PermissionTeacherDTO(
        Long id,
        String alias,
        String guardianName,
        String inviteeName,
        String childName,
        RelationType relation,
        PermissionRequestStatus permissionRequestStatus,
        LocalDateTime approvalDate
) {
}