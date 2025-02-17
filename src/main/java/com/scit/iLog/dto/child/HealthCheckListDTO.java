package com.scit.iLog.dto.child;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCheckListDTO {
	private Long id;
	private String childName;
	private String originalHealthCheckImgName;
	private String createdAt;
}
