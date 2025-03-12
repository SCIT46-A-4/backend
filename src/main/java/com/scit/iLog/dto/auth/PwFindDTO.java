package com.scit.iLog.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PwFindDTO {
    private boolean success;
    private String message;
}
