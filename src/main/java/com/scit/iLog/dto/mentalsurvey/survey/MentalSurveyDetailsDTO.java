package com.scit.iLog.dto.mentalsurvey.survey;

import java.time.LocalDateTime;
import java.util.List;

import com.scit.iLog.domain.RelationType;

public record MentalSurveyDetailsDTO(
                String id,
                String title,
                String description,
                RelationType type, // 2025-03-13 / 김은진 추가
                List<MentalSurveySectionDetailsDTO> sections,
                LocalDateTime createdAt) {
}
