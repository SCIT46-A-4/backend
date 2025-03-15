package com.scit.iLog.service.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisTargetEntity;
import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.ai.AIAnalysisResponseForWritingDTO;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.web.multipart.MultipartFile;

public interface AnalysisClient {
    AIAnalysisResponseDTO getAIAnalysisResponse(AnalysisTargetEntity analysisTarget);

    AIAnalysisResponseForWritingDTO getAIAnalysisResponseForWritings();

    ChildRecordExtraction getChildRecordData(String childDataExtractionPrompt, MultipartFile healthCheckImg);
}
