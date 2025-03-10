package com.scit.iLog.domain.permition;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;

import lombok.Builder;

@Builder
public record PermissionRequestDTO(
        Long id,
        Long requesterId,
        Long inviteeId,
        Long childId,
        RelationType relationType,
        PermissionRequestStatus permissionRequestStatus,
        String requestCodeLink
) {}