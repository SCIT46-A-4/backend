package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisType;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WritingAnalysisTargetInsertDTO(
        MultipartFile analysisTargetWriting,
        String analysisTargetText,
        LocalDateTime targetDate,
        String companion,
        String supplementaryComment,
        List<AnalysisType> analysisTypes,
        String weatherResponse
) {
}
