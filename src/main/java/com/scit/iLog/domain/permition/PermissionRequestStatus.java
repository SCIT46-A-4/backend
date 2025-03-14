package com.scit.iLog.domain.permition;

import lombok.Getter;

@Getter
public enum PermissionRequestStatus
	{
		PENDING("송신중"), ACCEPTED("수락됨"), EXPIRED("파기");
		
		private final String permitionStatus;
		
		PermissionRequestStatus(String permission)
		{
			this.permitionStatus = permission;
		}

	}