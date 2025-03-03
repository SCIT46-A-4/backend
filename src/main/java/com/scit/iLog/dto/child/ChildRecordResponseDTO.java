package com.scit.iLog.dto.child;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildRecordResponseDTO {
    private boolean success;
    private String redirectUrl;
}