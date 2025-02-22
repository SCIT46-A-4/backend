package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.analysis.ai.AiAnalysisResponseDTO;
import com.scit.iLog.dto.analysis.ai.AiAnalysisResponseForWritingDTO;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.web.multipart.MultipartFile;

public interface AnalysisClient {
    AiAnalysisResponseDTO getAiAnalysisResponse();
    AiAnalysisResponseForWritingDTO getAiAnalysisResponseForWritings();
    ChildRecordExtraction getChildRecordData(String childDataExtractionPrompt, MultipartFile healthCheckImg);
}
