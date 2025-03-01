package com.scit.iLog.dto.child;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildRecordUpdateResponseDTO 
{
	// C-7 -> C-5로 넘길 때 쓰는 DTO
    private boolean success;
    private String redirectUrl;
}