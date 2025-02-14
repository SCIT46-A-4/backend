package com.scit.iLog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCheckListDTO {
	private Long id;
	private String childName;
	private String originalSurveyFileName;
	private String createdAt;

}
