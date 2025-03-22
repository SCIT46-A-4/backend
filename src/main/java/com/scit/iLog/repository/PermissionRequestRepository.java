package com.scit.iLog.repository;

import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.permission.PermissionRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRequestRepository extends JpaRepository<PermissionRequestEntity, Long> {
    Optional<PermissionRequestEntity> findByRequestLinkCode(String requestLinkCode);

    List<PermissionRequestEntity> findAllByRequesterId(Long requesterId);

    List<PermissionRequestEntity> findAllByInviteeId(Long inviteeId);

    Optional<PermissionRequestEntity> findByIdAndChildAndAlias(Long permissionId, ChildEntity child, String alias);

    boolean existsByChildAndInvitee(ChildEntity child, MemberEntity invitee);
}
