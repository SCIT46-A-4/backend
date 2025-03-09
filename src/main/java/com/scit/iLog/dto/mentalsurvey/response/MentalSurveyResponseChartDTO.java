package com.scit.iLog.dto.mentalsurvey.response;

	import java.time.LocalDateTime;

import lombok.Builder;

	@Builder
	public record MentalSurveyResponseChartDTO(
			// 25/3/5 준: D-2 차트 보여주기 위한 DTO
			int totalLikertScore,
			String comment,
			String surveyTitle,
			LocalDateTime createdAt,
			Long respondentId
	) {
	}
