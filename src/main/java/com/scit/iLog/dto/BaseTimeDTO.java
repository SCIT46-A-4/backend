package com.scit.iLog.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseTimeDTO {
    protected LocalDateTime createdAt;
    protected LocalDateTime modifiedAt;
}
