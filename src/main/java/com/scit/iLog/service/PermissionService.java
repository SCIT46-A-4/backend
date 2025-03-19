package com.scit.iLog.service;

import com.scit.iLog.domain.PermissionLevel;
import com.scit.iLog.domain.RelationShipEntity;
import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.permission.PermissionRequestEntity;
import com.scit.iLog.domain.permission.PermissionRequestStatus;
import com.scit.iLog.repository.PermissionRequestRepository;
import com.scit.iLog.repository.RelationShipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRequestRepository permissionRequestRepository;
    private final RelationShipRepository relationShipRepository;

    @Transactional
    public boolean acceptPermission(long requestId) {
        PermissionRequestEntity permissionRequest = permissionRequestRepository.findById(requestId)
                .orElseThrow(EntityNotFoundException::new);
        permissionRequest.setPermissionStatus(PermissionRequestStatus.ACCEPTED);
        ChildEntity child = permissionRequest.getChild();
        MemberEntity invitee = permissionRequest.getInvitee();
        if (!relationShipRepository.existsByChildAndMember(child, invitee)) {
            RelationShipEntity relationShip = RelationShipEntity.builder()
                    .member(invitee)
                    .relationType(RelationType.TEACHER)
                    .child(child)
                    .permissionLevel(PermissionLevel.VIEWER)
                    .build();
            relationShipRepository.save(relationShip);
        }
        return true;
    }

    @Transactional
    public boolean abortPermission(Long permissionId) {
        PermissionRequestEntity permissionRequest = permissionRequestRepository.findById(permissionId)
                .orElseThrow(EntityNotFoundException::new);
        permissionRequest.setPermissionStatus(PermissionRequestStatus.ABORTED);
        return true;
    }
}
