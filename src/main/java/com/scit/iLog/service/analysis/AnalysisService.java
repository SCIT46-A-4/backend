package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.analysis.*;
import com.scit.iLog.dto.child.ChildRecordExtraction;
import org.springframework.web.multipart.MultipartFile;

public interface AnalysisService {
    ChildRecordExtraction getExtractChildRecordData(MultipartFile targetFile);
    TextExtractionDTO getExtractedText(Long analysisTargetId);

    Long getWritingAnalysisResult(Long analysisTargetId);
    Long getImageAnalysisResult(Long analysisTargetId);

    Long saveAnalysisTarget(Long memberId, Long childId, AnalysisTargetInsertDTO analysisTargetInsertDTO);
    void saveAnalysisResultNote(Long analysisResultId, AnalysisResultNoteInsertDTO analysisResultNoteInsertDTO);

    ImageAnalysisResultDetailsDTO getImageAnalysisResultDetails(Long analysisResultId);

    void saveTextExtraction(Long analysisTargetId, String text);

    void updateAnalyzedTextOfAnalysisTarget(Long analysisTargetId, WritingAnalysisTargetInsertDTO writingAnalysisTargetInsertDTO);
}
