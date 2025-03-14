package com.scit.iLog.dto.auth;

import java.time.LocalDateTime;

import com.scit.iLog.domain.RelationType;

import com.scit.iLog.domain.permission.PermissionRequestStatus;
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
public class PermissionRequestDTO {
	private Long id;
	private Long requesterId; 	// 보낸 멤버 ID
	private Long inviteeId; 	// 받은 멤버 ID
	private Long childId;
	private String childName; 	// 아이 이름
	private RelationType requesterRelationType;
	private RelationType inviteeRelationType;
	private PermissionRequestStatus permissionRequestStatus;
	private String requestCodeLink;
	private String alias; 		// 호칭
	LocalDateTime approvalDate; // 승인완료된 시간
}