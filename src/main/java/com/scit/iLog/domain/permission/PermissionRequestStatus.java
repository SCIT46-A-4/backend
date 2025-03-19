package com.scit.iLog.domain.permission;

import lombok.Getter;

@Getter
public enum PermissionRequestStatus {
    PENDING("송신중"),
    ACCEPTED("수락됨"),
    EXPIRED("파기"),
    ABORTED("거부됨");

    private final String permissionStatus;

    PermissionRequestStatus(String permission) {
        this.permissionStatus = permission;
    }

}