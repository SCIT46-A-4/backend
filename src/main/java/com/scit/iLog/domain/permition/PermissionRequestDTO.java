package com.scit.iLog.domain.permition;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.child.ChildEntity;
import com.scit.iLog.domain.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionRequestDTO
{
        private Long id;
        private Long requesterId;	//보낸 멤버 ID
        private Long inviteeId;		//받은 멤버 ID
        private Long childId;
        private RelationType relationType;
        private PermissionRequestStatus permissionRequestStatus;
        private String requestCodeLink;
        private String alias;		//호칭
}